import { Component, Input } from '@angular/core';
import { Contact } from '../../../core/models/models';

@Component({
  selector: 'app-chat-header',
  imports: [],
  templateUrl: './chat-header.component.html',
  styleUrl: './chat-header.component.css'
})
export class ChatHeaderComponent {
  @Input() selectedContact!: Contact;
}
