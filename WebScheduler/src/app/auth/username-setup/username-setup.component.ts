import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../_services/auth.service';
import { TokenStorageService } from '../../_services/token-storage.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-username-setup',
    templateUrl: './username-setup.component.html',
    styleUrls: ['./username-setup.component.css'],
    standalone: false
})
export class UsernameSetupComponent implements OnInit {
  form: any = {
    username: null
  };
  isSubmitted = false;
  errorMessage = '';
  userId: number | null = null;

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    const user = this.tokenStorage.getUser();
    if (!user || !user.id) {
      this.router.navigate(['/login']);
      return;
    }
    if (user.username && this.tokenStorage.getToken()) {
      this.router.navigate(['/home']);
      return;
    }
    this.userId = user.id;
  }

  onSubmit(): void {
    if (!this.userId) {
      return;
    }

    const { username } = this.form;

    const confirmMsg = this.translate.instant('USERNAME_SETUP.CONFIRM', { username });
    if (!confirm(confirmMsg)) {
      return;
    }

    this.authService.setUsername(this.userId, username).subscribe({
      next: data => {
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(data);
        window.location.href = '/home';
      },
      error: err => {
        this.errorMessage = err.error?.message || err.message;
        this.isSubmitted = true;
      }
    });
  }
}
