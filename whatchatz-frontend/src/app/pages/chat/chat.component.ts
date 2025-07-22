import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../core/service/auth.service';
import { WhatchatzRestService } from '../../core/service/whatchatz-rest.service';
import { WhatchatzSocketService } from '../../core/service/whatchatz-socket.service';
import { ContactListComponent } from '../../features/chat/contact-list/contact-list.component';
import { Contact, ContactWithMeta, Message, User } from '../../core/models/models';
import { NewContactPopupComponent } from '../../features/chat/new-contact-popup/new-contact-popup.component';
import { generateChatId } from '../../shared/helpers/uid.helpers';
import { ChatWindowComponent } from '../../features/chat/chat-window/chat-window.component';
import { LeftBarComponent } from '../../features/chat/left-bar/left-bar.component';

@Component({
  selector: 'app-chat',
  imports: [
    CommonModule,
    ContactListComponent,
    FormsModule,
    NewContactPopupComponent,
    ChatWindowComponent,
    LeftBarComponent,
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent implements OnInit, OnDestroy {
  contacts: ContactWithMeta[] = [];
  showAddContactModal = false;
  selectedContact: ContactWithMeta | null = null;
  messages: Message[] = [];
  private currentChatId: string | null = null;
  private messageNotificationSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private whatchatzRestService: WhatchatzRestService,
    private whatchatzSocketService: WhatchatzSocketService,
    private router: Router
  ) {}

  async ngOnInit() {
    await this.authService.waitForAuthState();
    await this.loadContacts();
    const token = await this.authService.getToken();
    if (!token) {
      console.error('No token found, user might not be authenticated');
      return;
    }
    this.whatchatzSocketService.connect(token);

    this.messageNotificationSubscription = this.whatchatzSocketService
      .getNewMessageNotifications()
      .subscribe((message: Message) => {
        if (this.currentChatId === message.chatId && this.currentChatId !== null) {
          this.reloadCurrentMessages();
        } else {
          for (const contact of this.contacts) {
            if (generateChatId(contact.contactId, contact.ownerId) === message.chatId) {
              contact.newMessagesCount += 1;
              break;
            }
          }
        }
      });
  }

  ngOnDestroy() {
    if (this.messageNotificationSubscription) {
      this.messageNotificationSubscription.unsubscribe();
    }
    this.whatchatzSocketService.disconnect();
  }

  onUserClick() {
    this.router.navigate(['/whatchatz/user']);
  }

  async loadContacts() {
    const token = await this.authService.getToken();
    if (token) {
      this.whatchatzRestService.getContactsFor(token).subscribe((contacts) => {
        this.contacts = contacts.map(contact => ({
          ...contact,
          newMessagesCount: 0
        })) as ContactWithMeta[];
      });
    }
  }

  async addContact(name: string) {
    const token = await this.authService.getToken();
    if (this.contacts.some(contact => contact.contactName === name)) {
      this.showAddContactModal = false;
      this.onContactSelected(this.contacts.find(contact => contact.contactName === name) as Contact);
      return;
    }
    if (token) {
      this.whatchatzRestService.searchUsers(token, name).subscribe((user) => {
        const userData = user as User;
        this.whatchatzRestService
          .addContact(token, userData.uid, name)
          .subscribe((contact) => {
            this.loadContacts();
            this.onContactSelected(contact as Contact);
            this.showAddContactModal = false;
          });
      });
    }
  }

  async onContactSelected(contact: Contact) {
    const selected = this.contacts.find(c => c.contactId === contact.contactId);
    if (selected) {
      this.selectedContact = selected;
    } else {
      this.selectedContact = { ...contact, newMessagesCount: 0 };
    }
    this.contacts.find(c => c.contactId === contact.contactId)!.newMessagesCount = 0;
    const token = await this.authService.getToken();
    const userId = await this.authService.getUserId();
    if (token && userId) {
      const chatId = generateChatId(contact.contactId, userId);
      this.currentChatId = chatId;

      this.whatchatzRestService
        .loadMessages(token, chatId)
        .subscribe((messages) => {
          this.messages = messages as Message[];
        });
    }
  }

  private async reloadCurrentMessages() {
    if (this.currentChatId) {
      const token = await this.authService.getToken();
      if (token) {
        this.whatchatzRestService
          .loadMessages(token, this.currentChatId)
          .subscribe((messages) => {
            this.messages = messages as Message[];
          });
      }
    }
  }

  async onSendMessage(message: string) {
    await this.whatchatzSocketService.send({
      message: message,
      receiverId: this.selectedContact?.contactId,
      chatId: this.currentChatId,
    });
    await this.reloadCurrentMessages();
  }
}
