import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { RoomCreateComponent } from './room-create/room-create.component';
import { RoomManageComponent } from './room-manage/room-manage.component';
import { RoomCalendarComponent } from './room-calendar/room-calendar.component';
import { RoomUserSelectComponent } from './room-user-select/room-user-select.component';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [
    HomeComponent,
    BoardUserComponent,
    BoardAdminComponent,
    RoomCreateComponent,
    RoomManageComponent,
    RoomCalendarComponent,
    RoomUserSelectComponent
  ],
  imports: [
    SharedModule,
    ReactiveFormsModule,
    RouterModule
  ]
})
export class SchedulerModule { }
