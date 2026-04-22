import { Component, OnInit, AfterViewInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../_services/auth.service';
import { TokenStorageService } from '../../_services/token-storage.service';
import { environment } from '../../../environments/environment';

declare var google: any;

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css'],
    standalone: false
})
export class LoginComponent implements OnInit, AfterViewInit {
  form: any = {
    username: null,
    password: null
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router,
    private ngZone: NgZone,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenStorage.getUser().roles;
    }
  }

  ngAfterViewInit(): void {
    if (!this.isLoggedIn) {
      this.initGoogleButton();
    }
  }

  initGoogleButton(): void {
    const btnElement = document.getElementById('google-signin-btn');
    if (typeof google !== 'undefined' && btnElement) {
      google.accounts.id.initialize({
        client_id: environment.googleClientId,
        callback: (response: any) => this.handleGoogleResponse(response)
      });
      google.accounts.id.renderButton(
        btnElement,
        { theme: 'outline', size: 'large', text: 'signin_with', width: '100%' }
      );
    } else {
      setTimeout(() => this.initGoogleButton(), 200);
    }
  }

  handleGoogleResponse(response: any): void {
    this.ngZone.run(() => {
      this.authService.googleAuth(response.credential).subscribe({
        next: data => {
          if (data.usernameRequired) {
            this.tokenStorage.saveUser(data);
            this.router.navigate(['/set-username']);
          } else {
            this.tokenStorage.saveToken(data.accessToken);
            this.tokenStorage.saveUser(data);
            this.isLoginFailed = false;
            this.isLoggedIn = true;
            this.roles = this.tokenStorage.getUser().roles;
            this.reloadPage();
          }
        },
        error: err => {
          this.errorMessage = this.mapAuthError(err, 'google');
          this.isLoginFailed = true;
        }
      });
    });
  }

  onSubmit(): void {
    const { username, password } = this.form;

    this.authService.login(username, password).subscribe({
      next: data => {
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(data);

        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser().roles;
        this.reloadPage();
      },
      error: err => {
        this.errorMessage = this.mapAuthError(err, 'credentials');
        this.isLoginFailed = true;
      }
    });
  }

  reloadPage(): void {
    // Full-page navigation so AppComponent re-bootstraps and the header
    // reflects the freshly authenticated state (user pill, invitations, …).
    window.location.href = '/home';
  }

  /**
   * Map an HttpErrorResponse to a precise, user-friendly translated message.
   * `kind` distinguishes between a failed username/password submit and a failed
   * Google OAuth exchange so we can surface the right generic fallback.
   */
  private mapAuthError(err: HttpErrorResponse | any, kind: 'credentials' | 'google'): string {
    const status = err?.status;
    const serverMsg: string =
      (typeof err?.error === 'string' ? err.error : err?.error?.message) || '';
    const lowered = serverMsg.toLowerCase();

    // Network / browser-side failure — request never reached the server.
    if (status === 0) {
      return this.translate.instant('LOGIN.NETWORK_ERROR');
    }

    // Rate limiting.
    if (status === 429) {
      return this.translate.instant('LOGIN.TOO_MANY_ATTEMPTS');
    }

    // Unauthorized — bad credentials.
    if (status === 401) {
      return this.translate.instant('LOGIN.INVALID_CREDENTIALS');
    }

    // Forbidden — account disabled/locked or access denied.
    if (status === 403) {
      if (lowered.includes('lock')) {
        return this.translate.instant('LOGIN.ACCOUNT_LOCKED');
      }
      return this.translate.instant('LOGIN.ACCOUNT_DISABLED');
    }

    // Server-side failure (5xx).
    if (typeof status === 'number' && status >= 500) {
      return this.translate.instant('LOGIN.SERVER_ERROR');
    }

    // 4xx not covered above — treat most as invalid credentials for the
    // credentials flow, or as a Google auth failure for the Google flow.
    if (typeof status === 'number' && status >= 400 && status < 500) {
      return kind === 'google'
        ? this.translate.instant('LOGIN.GOOGLE_AUTH_FAILED')
        : this.translate.instant('LOGIN.INVALID_CREDENTIALS');
    }

    // Anything else — unexpected.
    return kind === 'google'
      ? this.translate.instant('LOGIN.GOOGLE_AUTH_FAILED')
      : this.translate.instant('LOGIN.UNEXPECTED_ERROR');
  }
}
