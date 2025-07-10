import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Contact } from '../../../core/models/models';
import { ContactCardComponent } from '../contact-card/contact-card.component';

@Component({
  selector: 'app-contact-list',
  imports: [CommonModule, ContactCardComponent],
  templateUrl: './contact-list.component.html',
  styleUrl: './contact-list.component.css',
})
export class ContactListComponent {
  @Input() contacts: Contact[] = [];
  @Input() selectedContactId: number | null = null;
  @Output() contactSelected = new EventEmitter<Contact>();
  @Output() addContactRequested = new EventEmitter<void>();

  onContactClick(contact: Contact) {
    this.contactSelected.emit(contact);
  }

  onAddContactClick() {
    this.addContactRequested.emit();
  }

  formatLastMessage(lastMessage: Date): string {
    const date = new Date(lastMessage);
    return date.toLocaleDateString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }
}
