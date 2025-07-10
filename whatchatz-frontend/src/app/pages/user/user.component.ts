import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/service/auth.service';
import { LoginComponent } from '../../features/user/login/login.component';
import { RegisterComponent } from '../../features/user/register/register.component';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { WhatchatzRestService } from '../../core/service/whatchatz-rest.service';
import { User } from '../../core/models/models';


@Component({
  selector: 'app-user',
  imports: [LoginComponent, RegisterComponent, CommonModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css',
})
export class UserComponent implements OnInit {
  isLoggedIn$!: Observable<boolean>;
  authStateInitialized = false;
  user: User | null = null;

  constructor(private authService: AuthService, private whatchatzRestService: WhatchatzRestService) {
    this.isLoggedIn$ = this.authService.user$.pipe(
      map((user) => user !== null)
    );
  }

  async ngOnInit() {
    await this.authService.waitForAuthState();
    this.authStateInitialized = true;
    const token = await this.authService.getToken();
    if (token) {
      const userId = await this.authService.getUserId();
      if (userId) {
        this.whatchatzRestService.getUser(token, userId).subscribe((user) => {
          this.user = user;
        });
      }
    }
  }

  isUserLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }

  logout() {
    this.authService.logout();
  }
}
