import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RoomService } from 'src/app/_services/room.service';

@Component({
    selector: 'app-room-create',
    templateUrl: './room-create.component.html',
    standalone: false
})
export class RoomCreateComponent {

  name = '';
  description = '';
  error = '';

  constructor(private roomService: RoomService, private router: Router) { }

  create(): void {
    if (!this.name.trim()) {
      this.error = 'Naziv sobe je obavezan';
      return;
    }
    this.error = '';
    this.roomService.createRoom(this.name.trim(), this.description.trim()).subscribe({
      next: (room) => {
        this.router.navigate(['/room', room.id]);
      },
      error: () => {
        this.error = 'Greška pri kreiranju sobe';
      }
    });
  }
}
