export interface Contact {
  id: number;
  ownerId: string;
  contactId: string;
  contactName: string;
  lastMessage: Date;
}

export interface User {
  uid: string;
  name: string;
  info?: string;
}

export interface Message {
  id: number;
  chatId: string;
  senderId: string;
  receiverId: string;
  message: string;
  timeStamp: Date;
}

export interface ContactWithMeta extends Contact {
  newMessagesCount: number;
}