import { Component, OnInit, AfterViewInit, NgZone } from '@angular/core';
import { Router } from '@angular/router';
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
    private ngZone: NgZone
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
          this.errorMessage = err.error?.message || err.message;
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
        this.errorMessage = err.message;
        this.isLoginFailed = true;
      }
    });
  }

  reloadPage(): void {
    window.location.reload();
  }
}
