import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { AuthService } from './core/service/auth.service';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'whatchatz';

  constructor(private authService: AuthService, private router: Router) {}

  async ngOnInit(): Promise<void> {
    await this.authService.waitForAuthState();

    if (this.authService.isAuthenticated() && this.router.url !== '/whatchatz/user') {
      this.router.navigate(['/whatchatz/chat']);
    } else {
      this.router.navigate(['/whatchatz/user']);
    }
  }

  logout() {
    this.authService.logout();
  }
}
