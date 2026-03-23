import { NgModule } from '@angular/core';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ProfileComponent } from './profile/profile.component';
import { UsernameSetupComponent } from './username-setup/username-setup.component';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    UsernameSetupComponent,
  ],
  imports: [
    SharedModule,
    ReactiveFormsModule,
    RouterModule
  ]
})
export class AuthModule { }
