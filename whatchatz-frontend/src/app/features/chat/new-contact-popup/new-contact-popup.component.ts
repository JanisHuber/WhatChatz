import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-new-contact-popup',
  imports: [FormsModule, CommonModule],
  templateUrl: './new-contact-popup.component.html',
  styleUrl: './new-contact-popup.component.css'
})
export class NewContactPopupComponent {
  @Output() closePopup = new EventEmitter<void>();
  @Output() addContact = new EventEmitter<string>();

  newContactName: string = '';

  onClosePopup() {
    this.closePopup.emit();
  }

  onAddContact() {
    this.addContact.emit(this.newContactName);
  }
}