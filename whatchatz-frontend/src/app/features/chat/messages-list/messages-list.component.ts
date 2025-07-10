import { Component, Input } from '@angular/core';
import { Contact, Message } from '../../../core/models/models';
import { MessageCardComponent } from '../message-card/message-card.component';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-messages-list',
  imports: [MessageCardComponent, CommonModule],
  templateUrl: './messages-list.component.html',
  styleUrl: './messages-list.component.css'
})
export class MessagesListComponent {
  @Input() messages: Message[] = [];
  @Input() selectedContact!: Contact;
}
