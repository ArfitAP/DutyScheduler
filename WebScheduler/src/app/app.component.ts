import { Component } from '@angular/core';
import { IRoomInvitation, IRoomJoinRequest } from './_models/Room';
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
  joinRequests: IRoomJoinRequest[] = [];
  showJoinRequests = false;

  constructor(private tokenStorageService: TokenStorageService, private roomService: RoomService) { }

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
    if (this.showInvitations) this.showJoinRequests = false;
  }

  loadJoinRequests(): void {
    this.roomService.getMyJoinRequests().subscribe({
      next: (data) => { this.joinRequests = data; },
      error: () => { this.joinRequests = []; }
    });
  }

  toggleJoinRequests(): void {
    this.showJoinRequests = !this.showJoinRequests;
    if (this.showJoinRequests) this.showInvitations = false;
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
