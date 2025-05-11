import { CommonModule, formatDate } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { Observable, of } from 'rxjs';
import { AppointmentService } from '../../core/service/appointment.service';
import { AuthService } from '../../core/service/auth.service';
import { IAppointment } from '../../model/appointment.model';
import { Page } from '../../model/page.model';
import { UserRole } from '../../model/user-role.enum';
import { AppointmentStatusPipe } from '../../shared/pipes/appointment-status.pipe';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    AppointmentStatusPipe,
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
    FormsModule,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  private appointmentService = inject(AppointmentService);
  private authService = inject(AuthService);

  pageInfo: Page<IAppointment> = {
    content: [],
    page: { size: 10, number: 0, totalElements: 0, totalPages: 1 },
  };

  appointments$!: Observable<IAppointment[]>;
  selectedDate: Date | null = null;
  dentistLogged: boolean = true;

  constructor() {
    this.dentistLogged = this.authService.getRole() === UserRole.Dentist;
    this.loadAppointments(0);
  }

  loadAppointments(page: number): void {
    this.appointmentService.findAll(page, 10).subscribe((response) => {
      this.pageInfo = response;
      this.appointments$ = of(response.content);
    });
  }

  goToPage(page: number): void {
    this.loadAppointments(page);
  }

  filteredByDate() {
    if (!this.selectedDate) return;

    const date = formatDate(this.selectedDate, 'yyyy-MM-dd', 'en-US');
    const currentPage = this.pageInfo.page.number;
    this.appointmentService.findByDate(currentPage, 10, date).subscribe((response) => {
      this.pageInfo = response;
      this.appointments$ = of(response.content);
    });
  }

  clearFilter(): void {
    this.selectedDate = null;
    this.loadAppointments(0);
  }

  onViewAppointment(appointment: IAppointment) {}

  onEditAppointment(appointment: IAppointment) {}

  onCancelAppointment(appointment: IAppointment) {}
}
