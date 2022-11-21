import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';



@NgModule({
  declarations: [
    HomeComponent,
    BoardUserComponent,
    BoardAdminComponent
  ],
  imports: [
    SharedModule,
    ReactiveFormsModule
  ]
})
export class SchedulerModule { }
