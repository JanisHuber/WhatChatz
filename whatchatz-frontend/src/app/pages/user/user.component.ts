import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/service/auth.service';
import { LoginComponent } from '../../features/user/login/login.component';
import { RegisterComponent } from '../../features/user/register/register.component';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-user',
  imports: [LoginComponent, RegisterComponent, CommonModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css',
})
export class UserComponent implements OnInit {
  isLoggedIn$!: Observable<boolean>;
  authStateInitialized = false;

  constructor(private authService: AuthService) {
    this.isLoggedIn$ = this.authService.user$.pipe(
      map((user) => user !== null)
    );
  }

  async ngOnInit() {
    await this.authService.waitForAuthState();
    this.authStateInitialized = true;
  }

  isUserLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }
}
