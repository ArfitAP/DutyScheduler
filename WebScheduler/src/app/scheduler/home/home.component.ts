import { Component, OnInit } from '@angular/core';
import { IRoom } from 'src/app/_models/Room';
import { RoomService } from 'src/app/_services/room.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css'],
    standalone: false
})
export class HomeComponent implements OnInit {

  isLoggedIn = false;
  rooms: IRoom[] = [];

  constructor(
    private tokenStorageService: TokenStorageService,
    private roomService: RoomService
  ) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    if (this.isLoggedIn) {
      this.loadRooms();
    }
  }

  loadRooms(): void {
    this.roomService.getMyRooms().subscribe({
      next: (data) => { this.rooms = data; },
      error: () => { this.rooms = []; }
    });
  }
}
