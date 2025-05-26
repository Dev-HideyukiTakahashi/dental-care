import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { AbsenceScheduleComponent } from './features/absence-schedule/absence-schedule.component';
import { AppointmentComponent } from './features/appointment/appointment.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { DentistListComponent } from './features/dentist-list/dentist-list.component';
import { DentistPasswordUpdateComponent } from './features/dentist-password-update/dentist-password-update.component';
import { HomeComponent } from './features/home/home.component';
import { MainComponent } from './features/main/main.component';
import { PatientEditComponent } from './features/patient-edit/patient-edit.component';
import { PatientListComponent } from './features/patient-list/patient-list.component';
import { RatingsComponent } from './features/rating/rating.component';

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
      {
        path: 'edit-patient',
        component: PatientEditComponent,
        canActivate: [authGuard],
        data: { roles: ['patient'] },
      },
      {
        path: 'rating',
        component: RatingsComponent,
        canActivate: [authGuard],
        data: { roles: ['patient'] },
      },
      {
        path: 'appointment',
        component: AppointmentComponent,
        canActivate: [authGuard],
        data: { roles: ['patient', 'admin'] },
      },
      {
        path: 'dentist/update-password',
        component: DentistPasswordUpdateComponent,
        canActivate: [authGuard],
        data: { roles: ['dentist'] },
      },
    ],
  },
];
