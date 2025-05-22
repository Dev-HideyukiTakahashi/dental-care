import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { AbsenceScheduleComponent } from './features/absence-schedule/absence-schedule.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { DentistListComponent } from './features/dentist-list/dentist-list.component';
import { HomeComponent } from './features/home/home.component';
import { MainComponent } from './features/main/main.component';
import { PatientListComponent } from './features/patient-list/patient-list.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'recover-password/:token', component: ResetPasswordComponent },

  {
    path: '',
    component: MainComponent,
    canActivate: [authGuard],
    children: [
      { path: '', component: HomeComponent },
      {
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [authGuard],
        data: { roles: ['admin'] },
      },
      {
        path: 'dentists',
        component: DentistListComponent,
        canActivate: [authGuard],
        data: { roles: ['admin'] },
      },
      {
        path: 'patients',
        component: PatientListComponent,
        canActivate: [authGuard],
        data: { roles: ['admin'] },
      },
      {
        path: 'schedule',
        component: AbsenceScheduleComponent,
        canActivate: [authGuard],
        data: { roles: ['admin', 'dentist'] },
      },
    ],
  },
];
