import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-message-input',
  imports: [FormsModule, CommonModule],
  templateUrl: './message-input.component.html',
  styleUrl: './message-input.component.css'
})
export class MessageInputComponent {
  @Output() sendMessage = new EventEmitter<string>();

  message: string = '';

  onSendMessage() {
    this.sendMessage.emit(this.message);
    this.message = '';
  }
}
