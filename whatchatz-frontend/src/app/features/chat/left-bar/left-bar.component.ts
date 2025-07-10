import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-left-bar',
  imports: [CommonModule],
  templateUrl: './left-bar.component.html',
  styleUrl: './left-bar.component.css',
})
export class LeftBarComponent {
  @Output() userClick = new EventEmitter<void>();

  onUserClick() {
    this.userClick.emit();
  }
}
