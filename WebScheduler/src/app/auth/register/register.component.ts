import { Component, OnInit, AfterViewInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
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
    private ngZone: NgZone,
    private translate: TranslateService
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
          this.googleErrorMessage = this.mapRegisterError(err, 'google');
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
        this.errorMessage = this.mapRegisterError(err, 'credentials');
        this.isSignUpFailed = true;
      }
    });
  }

  /**
   * Map an HttpErrorResponse from the signup endpoints to a precise,
   * user-friendly translated message. For 400/409 errors we peek at the
   * server payload to distinguish "username already taken" from "email
   * already taken" — the backend returns these as simple messages.
   */
  private mapRegisterError(err: HttpErrorResponse | any, kind: 'credentials' | 'google'): string {
    const status = err?.status;
    const serverMsg: string =
      (typeof err?.error === 'string' ? err.error : err?.error?.message) || '';
    const lowered = serverMsg.toLowerCase();

    // Network / browser-side failure — request never reached the server.
    if (status === 0) {
      return this.translate.instant('REGISTER.NETWORK_ERROR');
    }

    // Server-side failure (5xx).
    if (typeof status === 'number' && status >= 500) {
      return this.translate.instant('REGISTER.SERVER_ERROR');
    }

    // Duplicate username / email, or other validation errors (4xx).
    if (typeof status === 'number' && status >= 400 && status < 500) {
      if (lowered.includes('username') && (lowered.includes('taken') || lowered.includes('exist') || lowered.includes('use'))) {
        return this.translate.instant('REGISTER.USERNAME_TAKEN');
      }
      if (lowered.includes('email') && (lowered.includes('taken') || lowered.includes('exist') || lowered.includes('use'))) {
        return this.translate.instant('REGISTER.EMAIL_TAKEN');
      }
      return kind === 'google'
        ? this.translate.instant('REGISTER.GOOGLE_AUTH_FAILED')
        : this.translate.instant('REGISTER.INVALID_INPUT');
    }

    // Anything else — unexpected.
    return kind === 'google'
      ? this.translate.instant('REGISTER.GOOGLE_AUTH_FAILED')
      : this.translate.instant('REGISTER.UNEXPECTED_ERROR');
  }
}
