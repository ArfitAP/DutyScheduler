import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { IUserRole } from 'src/app/_models/UserRole';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { AppSettings } from 'src/app/_services/app.settings';

@Component({
    selector: 'app-board-admin',
    templateUrl: './board-admin.component.html',
    styleUrls: ['./board-admin.component.css'],
    standalone: false
})
export class BoardAdminComponent implements OnInit {

  isLoggedIn = false;
  username: string = '';
  requestedUser: IUserRole | null = null;

  constructor(private tokenStorageService: TokenStorageService, private http: HttpClient, private translate: TranslateService) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
  }

  getUserRole(): void {
    if (this.username.length == 0) return;

    this.http.get(AppSettings.API_ENDPOINT + "schedule/getUserRole/" + this.username, { responseType: 'text' })
      .subscribe({
        next: data => { this.requestedUser = JSON.parse(data); },
        error: () => { }
      });
  }

  setAdmin(): void {
    this.http.post(AppSettings.API_ENDPOINT + "schedule/setUserRole/", this.requestedUser, { responseType: 'text' })
      .subscribe({
        next: data => {
          let res: Boolean = JSON.parse(data);
          if (res == true) {
            alert(this.translate.instant('ADMIN.USER_SAVED'));
            this.requestedUser = null;
            this.username = '';
          } else alert(this.translate.instant('ADMIN.ERROR'));
        },
        error: () => { alert(this.translate.instant('ADMIN.ERROR')); }
      });
  }
}
