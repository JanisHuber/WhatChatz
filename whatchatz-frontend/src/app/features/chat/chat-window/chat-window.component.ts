import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Contact, Message } from '../../../core/models/models';
import { MessageInputComponent } from '../message-input/message-input.component';
import { CommonModule } from '@angular/common';
import { ChatHeaderComponent } from '../chat-header/chat-header.component';
import { MessagesListComponent } from '../messages-list/messages-list.component';


@Component({
  selector: 'app-chat-window',
  imports: [MessagesListComponent, MessageInputComponent, CommonModule, ChatHeaderComponent],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css'
})
export class ChatWindowComponent {
  @Input() selectedContact: Contact | null = null;
  @Input() messages: Message[] = [];

  @Output() sendMessage = new EventEmitter<string>();

  onSendMessage(message: string) {
    this.sendMessage.emit(message);
  } 
}
