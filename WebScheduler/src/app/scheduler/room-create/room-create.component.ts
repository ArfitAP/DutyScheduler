import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
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

  constructor(private roomService: RoomService, private router: Router, private translate: TranslateService) { }

  create(): void {
    if (!this.name.trim()) {
      this.error = this.translate.instant('ROOM_CREATE.NAME_REQUIRED');
      return;
    }
    this.error = '';
    this.roomService.createRoom(this.name.trim(), this.description.trim()).subscribe({
      next: (room) => {
        this.router.navigate(['/room', room.id]);
      },
      error: () => {
        this.error = this.translate.instant('ROOM_CREATE.CREATE_ERROR');
      }
    });
  }
}
