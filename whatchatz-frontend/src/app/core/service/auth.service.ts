import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import {
  createUserWithEmailAndPassword,
  getAuth,
  onAuthStateChanged,
  signInWithEmailAndPassword,
  signOut,
  User,
} from 'firebase/auth';
import { Router } from '@angular/router';
import { WhatchatzRestService } from './whatchatz-rest.service';


@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUser: User | null = null;
  private auth = getAuth();
  private authStateInitialized = false;
  private userSubject = new BehaviorSubject<User | null>(null);
  public user$ = this.userSubject.asObservable();

  constructor(private router: Router, private whatchatzRestService: WhatchatzRestService) {
    onAuthStateChanged(this.auth, (user) => {
      this.currentUser = user;
      this.authStateInitialized = true;
      this.userSubject.next(user);
    });
  }

  isAuthenticated(): boolean {
    return this.currentUser !== null;
  }


  waitForAuthState(): Promise<User | null> {
    if (this.authStateInitialized) {
      return Promise.resolve(this.currentUser);
    }

    return new Promise((resolve) => {
      const unsubscribe = onAuthStateChanged(this.auth, (user) => {
        this.authStateInitialized = true;
        resolve(user);
        unsubscribe();
      });
    });
  }

  async getToken(): Promise<string | null> {
    if (!this.currentUser) return null;
    return await this.currentUser.getIdToken();
  }

  async getUserId(): Promise<string | null> {
    if (!this.currentUser) return null;
    return this.currentUser.uid;
  }

  async login(email: string, password: string): Promise<void> {
    try {
      await signInWithEmailAndPassword(this.auth, email, password);
    } catch (error) {
      throw error;
    }
  }

  async register(email: string, password: string, name: string, info: string): Promise<void> {
    try {
      await createUserWithEmailAndPassword(this.auth, email, password);
      const token = await this.currentUser?.getIdToken(true);
      try {
        const response = await this.whatchatzRestService.saveUser(token || '', name, info).toPromise();
        console.log('Backend response from saveUser:', response);
      } catch (error) {
        console.error('Error saving user:', error);
        throw error;
      }
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  }

  logout(): void {
    signOut(this.auth);
    this.router.navigate(['/whatchatz/user']);
  }
}
