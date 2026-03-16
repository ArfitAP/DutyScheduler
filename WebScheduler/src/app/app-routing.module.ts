import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';
import { HomeComponent } from './scheduler/home/home.component';
import { ProfileComponent } from './auth/profile/profile.component';
import { BoardAdminComponent } from './scheduler/board-admin/board-admin.component';
import { RoomCreateComponent } from './scheduler/room-create/room-create.component';
import { RoomManageComponent } from './scheduler/room-manage/room-manage.component';
import { RoomCalendarComponent } from './scheduler/room-calendar/room-calendar.component';
import { RoomUserSelectComponent } from './scheduler/room-user-select/room-user-select.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'admin', component: BoardAdminComponent },
  { path: 'room/create', component: RoomCreateComponent },
  { path: 'room/:id', component: RoomManageComponent },
  { path: 'room/:id/calendar', component: RoomCalendarComponent },
  { path: 'room/:id/select', component: RoomUserSelectComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
