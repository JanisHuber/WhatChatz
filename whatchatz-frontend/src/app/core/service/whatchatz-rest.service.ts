import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Contact, User } from '../models/models';
import { catchError, tap } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WhatchatzRestService {
  private apiUrl = 'http://localhost:9080/whatchatz';

  constructor(private http: HttpClient) {}

  getContactsFor(token: string) {
    return this.http.get<Contact[]>(this.apiUrl + '/api/contacts', {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      }),
    });
  }

  searchUsers(token: string, queryName: string) {
    return this.http.get<User>(this.apiUrl + '/api/users', {
      params: {
        queryName: queryName,
      },
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      }),
    });
  }

  saveUser(token: string, name: string, info: string) {
    return this.http.post(this.apiUrl + '/api/users/new',
        {
          name: name,
          info: info,
        },
        {
          headers: new HttpHeaders({
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          }),
        }
      )
      .pipe(
        catchError((error) => {
          return throwError(() => error);
        })
      );
  }

  getUser(token: string, userUid: string): Observable<User> {
    return this.http.get<User>(this.apiUrl + '/api/users/' + userUid, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      }),
    });
  }

  addContact(token: string, contactId: string, contactName: string) {
    return this.http.post(
      this.apiUrl + '/api/contacts/new',
      {
        contactId: contactId,
        contactName: contactName,
      },
      {
        headers: new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        }),
      }
    );
  }

  loadMessages(token: string, chatId: string) {
    return this.http.get(this.apiUrl + '/api/chats/' + chatId + '/messages', {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      }),
    });
  }
}
