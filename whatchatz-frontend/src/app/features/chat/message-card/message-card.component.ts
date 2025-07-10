import { Component, Input } from '@angular/core';
import { Message } from '../../../core/models/models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-message-card',
  imports: [DatePipe],
  templateUrl: './message-card.component.html',
  styleUrl: './message-card.component.css'
})
export class MessageCardComponent {
  @Input() message!: Message;
  @Input() isSender!: boolean;

  OuterDivClassBlue: string = 'flex justify-end';
  OuterDivClassGray: string = 'flex justify-start';

  InnerDivClassBlue: string = 'max-w-xs lg:max-w-md px-4 py-2 rounded-2xl bg-blue-500 text-white';
  InnerDivClassGray: string = 'max-w-xs lg:max-w-md px-4 py-2 rounded-2xl bg-gray-400 text-gray-800';

  pClassBlue: string = 'text-xs text-blue-100 mt-1';
  pClassGray: string = 'text-xs text-gray-600 mt-1';
}
