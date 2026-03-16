import { Component } from '@angular/core';
import { IRoomInvitation } from './_models/Room';
import { RoomService } from './_services/room.service';
import { TokenStorageService } from './_services/token-storage.service';

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

  constructor(private tokenStorageService: TokenStorageService, private roomService: RoomService) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();

    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUser();
      this.roles = user.roles;
      this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      this.username = user.username;
      this.loadInvitations();
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
  }

  acceptInvitation(id: number): void {
    this.roomService.acceptInvitation(id).subscribe({
      next: () => { this.loadInvitations(); },
      error: () => { alert('Greška!'); }
    });
  }

  declineInvitation(id: number): void {
    this.roomService.declineInvitation(id).subscribe({
      next: () => { this.loadInvitations(); },
      error: () => { alert('Greška!'); }
    });
  }

  logout(): void {
    this.tokenStorageService.signOut();
    window.location.reload();
  }
}
