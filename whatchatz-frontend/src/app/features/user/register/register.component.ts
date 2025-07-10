import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/service/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';
  name: string = '';
  info: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  async onSubmit() {
    try {
      this.errorMessage = '';
      await this.authService.register(this.email, this.password, this.name, this.info);
      this.router.navigate(['/whatchatz/chat']);
    } catch (error: any) {
      this.errorMessage = error.message || 'Registrierung fehlgeschlagen';
      console.error('Registrierungsfehler:', error);
    }
  }
}
