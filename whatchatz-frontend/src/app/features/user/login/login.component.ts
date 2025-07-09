import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  async onSubmit() {
    try {
      this.errorMessage = '';
      await this.authService.login(this.email, this.password);
      this.router.navigate(['/whatchatz/chat']);
    } catch (error: any) {
      this.errorMessage = error.message || 'Login fehlgeschlagen';
      console.error('Login-Fehler:', error);
    }
  }
}
