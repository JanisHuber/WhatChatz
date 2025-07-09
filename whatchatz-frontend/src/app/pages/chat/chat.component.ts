import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/service/auth.service';
import { WhatchatzRestService } from '../../core/service/whatchatz-rest.service';

@Component({
  selector: 'app-chat',
  imports: [],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent implements OnInit {
  
  constructor(private authService: AuthService, private whatchatzRestService: WhatchatzRestService) {}

  async ngOnInit() {
    await this.authService.waitForAuthState();
    const token = await this.authService.getToken();
    if (token) {
      this.whatchatzRestService.getContactsFor(token).subscribe(contacts => {
        console.log(contacts);
      }); 
    }
  }


  
}
