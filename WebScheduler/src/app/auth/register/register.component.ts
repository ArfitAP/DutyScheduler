import { Component, OnInit, AfterViewInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../_services/auth.service';
import { TokenStorageService } from '../../_services/token-storage.service';
import { environment } from '../../../environments/environment';

declare var google: any;

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css'],
    standalone: false
})
export class RegisterComponent implements OnInit, AfterViewInit {
  form: any = {
    username: null,
    email: null,
    password: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  isGoogleSignUpFailed = false;
  errorMessage = '';
  googleErrorMessage = '';

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router,
    private ngZone: NgZone
  ) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    if (!this.isSuccessful) {
      this.initGoogleButton();
    }
  }

  initGoogleButton(): void {
    const btnElement = document.getElementById('google-signup-btn');
    if (typeof google !== 'undefined' && btnElement) {
      google.accounts.id.initialize({
        client_id: environment.googleClientId,
        callback: (response: any) => this.handleGoogleResponse(response)
      });
      google.accounts.id.renderButton(
        btnElement,
        { theme: 'outline', size: 'large', text: 'signup_with', width: '100%' }
      );
    } else {
      setTimeout(() => this.initGoogleButton(), 200);
    }
  }

  handleGoogleResponse(response: any): void {
    this.ngZone.run(() => {
      this.authService.googleAuth(response.credential, true).subscribe({
        next: data => {
          if (data.usernameRequired) {
            this.tokenStorage.saveUser(data);
            this.router.navigate(['/set-username']);
          } else {
            this.tokenStorage.saveToken(data.accessToken);
            this.tokenStorage.saveUser(data);
            window.location.reload();
          }
        },
        error: err => {
          this.googleErrorMessage = err.error?.message || err.message;
          this.isGoogleSignUpFailed = true;
        }
      });
    });
  }

  onSubmit(): void {
    const { username, email, password } = this.form;

    this.authService.register(username, email, password).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      error: err => {
        this.errorMessage = err.message;
        this.isSignUpFailed = true;
      }
    });
  }
}
