import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivate } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  async canActivate(): Promise<boolean> {
    await this.authService.waitForAuthState();

    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/whatchatz/user']);
      return false;
    }

    return true;
  }
}
