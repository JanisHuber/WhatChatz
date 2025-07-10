import { Component, Input } from '@angular/core';
import { Contact } from '../../../core/models/models';

@Component({
  selector: 'app-contact-card',
  imports: [],
  templateUrl: './contact-card.component.html',
  styleUrl: './contact-card.component.css',
})
export class ContactCardComponent {
  @Input() contact!: Contact;

  formatLastMessage(lastMessage: Date): string {
    const date = new Date(lastMessage);
    return date.toLocaleDateString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }
}
