import { Component } from '@angular/core';
import { IRoomInvitation, IRoomJoinRequest } from './_models/Room';
import { LanguageService } from './_services/language.service';
import { RoomService } from './_services/room.service';
import { TokenStorageService } from './_services/token-storage.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    standalone: false
})
export class AppComponent {
  private roles: string[] = [];
  isLoggedIn = false;
  showAdminBoard = false;
  username?: string;
  invitations: IRoomInvitation[] = [];
  showInvitations = false;
  joinRequests: IRoomJoinRequest[] = [];
  showJoinRequests = false;
  showLanguageMenu = false;

  constructor(
    private tokenStorageService: TokenStorageService,
    private roomService: RoomService,
    public languageService: LanguageService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();

    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUser();
      this.roles = user.roles;
      this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      this.username = user.username;
      this.loadInvitations();
      this.loadJoinRequests();
    }
  }

  loadInvitations(): void {
    this.roomService.getPendingInvitations().subscribe({
      next: (data) => { this.invitations = data; },
      error: () => { this.invitations = []; }
    });
  }

  toggleInvitations(): void {
    this.showInvitations = !this.showInvitations;
    if (this.showInvitations) { this.showJoinRequests = false; this.showLanguageMenu = false; }
  }

  loadJoinRequests(): void {
    this.roomService.getMyJoinRequests().subscribe({
      next: (data) => { this.joinRequests = data; },
      error: () => { this.joinRequests = []; }
    });
  }

  toggleJoinRequests(): void {
    this.showJoinRequests = !this.showJoinRequests;
    if (this.showJoinRequests) { this.showInvitations = false; this.showLanguageMenu = false; }
  }

  toggleLanguageMenu(): void {
    this.showLanguageMenu = !this.showLanguageMenu;
    if (this.showLanguageMenu) { this.showInvitations = false; this.showJoinRequests = false; }
  }

  changeLanguage(lang: string): void {
    this.languageService.setLanguage(lang);
    this.showLanguageMenu = false;
  }

  acceptInvitation(id: number): void {
    this.roomService.acceptInvitation(id).subscribe({
      next: () => { this.loadInvitations(); },
      error: () => { alert(this.translate.instant('NAV.ERROR')); }
    });
  }

  declineInvitation(id: number): void {
    this.roomService.declineInvitation(id).subscribe({
      next: () => { this.loadInvitations(); },
      error: () => { alert(this.translate.instant('NAV.ERROR')); }
    });
  }

  logout(): void {
    this.tokenStorageService.signOut();
    window.location.reload();
  }
}
