import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password.component';
import { HomeComponent } from './features/home/home.component';
import { MainComponent } from './features/main/main.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'recover-password/:token', component: ResetPasswordComponent },

  {
    path: '',
    component: MainComponent,
    canActivate: [authGuard],
    children: [{ path: '', component: HomeComponent }],
  },
];
