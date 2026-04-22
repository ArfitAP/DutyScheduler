import { Component, HostListener, OnInit } from '@angular/core';
import { trigger, transition, style, query, animate, group } from '@angular/animations';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { IRoomInvitation, IRoomJoinRequest } from './_models/Room';
import { LanguageService } from './_services/language.service';
import { RoomService } from './_services/room.service';
import { ThemeService, ThemeMode } from './_services/theme.service';
import { TokenStorageService } from './_services/token-storage.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: false,
  animations: [
    trigger('routeAnim', [
      transition('* <=> *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(14px)' })
        ], { optional: true }),
        query(':leave', [
          style({ position: 'absolute', width: '100%', opacity: 1 }),
          animate('160ms cubic-bezier(0.16, 1, 0.3, 1)',
            style({ opacity: 0, transform: 'translateY(-8px)' }))
        ], { optional: true }),
        query(':enter', [
          animate('320ms 60ms cubic-bezier(0.16, 1, 0.3, 1)',
            style({ opacity: 1, transform: 'translateY(0)' }))
        ], { optional: true })
      ])
    ])
  ]
})
export class AppComponent implements OnInit {
  private roles: string[] = [];
  isLoggedIn = false;
  showAdminBoard = false;
  username?: string;
  invitations: IRoomInvitation[] = [];
  joinRequests: IRoomJoinRequest[] = [];

  showInvitations = false;
  showJoinRequests = false;
  showLanguageMenu = false;
  showUserMenu = false;
  showMobileMenu = false;
  scrolled = false;
  currentYear = new Date().getFullYear();

  constructor(
    private tokenStorageService: TokenStorageService,
    private roomService: RoomService,
    public languageService: LanguageService,
    public themeService: ThemeService,
    private translate: TranslateService,
    private router: Router
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

    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => {
        this.closeAllMenus();
        this.showMobileMenu = false;
        window.scrollTo({ top: 0, behavior: 'instant' as ScrollBehavior });
      });
  }

  @HostListener('window:scroll')
  onScroll(): void {
    this.scrolled = window.scrollY > 10;
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeAllMenus();
    this.showMobileMenu = false;
  }

  getRouteKey(): string {
    return this.router.url;
  }

  closeAllMenus(): void {
    this.showInvitations = false;
    this.showJoinRequests = false;
    this.showLanguageMenu = false;
    this.showUserMenu = false;
  }

  toggleMobileMenu(): void {
    this.showMobileMenu = !this.showMobileMenu;
    if (this.showMobileMenu) this.closeAllMenus();
  }

  loadInvitations(): void {
    this.roomService.getPendingInvitations().subscribe({
      next: (data) => { this.invitations = data; },
      error: () => { this.invitations = []; }
    });
  }

  toggleInvitations(): void {
    const next = !this.showInvitations;
    this.closeAllMenus();
    this.showInvitations = next;
  }

  loadJoinRequests(): void {
    this.roomService.getMyJoinRequests().subscribe({
      next: (data) => { this.joinRequests = data; },
      error: () => { this.joinRequests = []; }
    });
  }

  toggleJoinRequests(): void {
    const next = !this.showJoinRequests;
    this.closeAllMenus();
    this.showJoinRequests = next;
  }

  toggleLanguageMenu(): void {
    const next = !this.showLanguageMenu;
    this.closeAllMenus();
    this.showLanguageMenu = next;
  }

  toggleUserMenu(): void {
    const next = !this.showUserMenu;
    this.closeAllMenus();
    this.showUserMenu = next;
  }

  changeLanguage(lang: string): void {
    this.languageService.setLanguage(lang);
    this.showLanguageMenu = false;
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }

  setThemeMode(mode: ThemeMode): void {
    this.themeService.setMode(mode);
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
