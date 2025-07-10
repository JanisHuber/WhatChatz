import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { Message } from '../models/models';

@Injectable({ providedIn: 'root' })
export class WhatchatzSocketService {
  private socket!: WebSocket;
  private newMessageSubject = new Subject<Message>();

  public connect(chatId: string, token: string): void {
    const wsUrl = `ws://localhost:9080/ws/chat/${chatId}?token=${token}`;
    this.socket = new WebSocket(wsUrl);

    this.socket.onopen = () => {
    };

    this.socket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      console.log('Nachricht erhalten:', data);

      if (data.type === 'NEW_MESSAGE') {
        this.newMessageSubject.next(data);
      }
    };

    this.socket.onerror = (err) => {
      console.error('WebSocket-Fehler', err);
    };

    this.socket.onclose = (ev) => {
    };
  }

  public send(msg: any): void {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      const payload = {
        message: msg.message,
        receiverId: msg.receiverId,
      };
      const json = JSON.stringify(payload);
      this.socket.send(json);
    } else {
      console.warn('Socket nicht bereit, cannot send:', msg);
    }
  }

  public getNewMessageNotifications(): Observable<Message> {
    return this.newMessageSubject.asObservable();
  }

  public disconnect(): void {
    if (this.socket) {
      this.socket.close();
    }
  }
}
