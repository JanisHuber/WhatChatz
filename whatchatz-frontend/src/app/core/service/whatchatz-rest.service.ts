import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Contact } from "../models/contact";

@Injectable({ providedIn: 'root' })
export class WhatchatzRestService {
  private apiUrl = 'http://localhost:9080/whatchatz';

  constructor(private http: HttpClient) {}

  getContactsFor(token:string) {
    return this.http.get<Contact[]>(this.apiUrl + '/api/contacts', {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      })
    });
  }
}
