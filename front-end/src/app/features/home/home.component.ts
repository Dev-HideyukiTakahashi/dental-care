import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { AppointmentService } from '../../core/service/appointment.service';
import { IAppointment } from '../../model/appointment.model';
import { Page } from '../../model/page.model';
import { AppointmentStatusPipe } from '../../shared/pipes/appointment-status.pipe';

@Component({
  selector: 'app-home',
  imports: [CommonModule, AppointmentStatusPipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  appointmentService = inject(AppointmentService);

  pageInfo: Page<IAppointment> = {
    content: [],
    page: { size: 10, number: 0, totalElements: 0, totalPages: 1 },
  };
  appointments$!: Observable<IAppointment[]>;

  constructor() {
    this.loadAppointments(0);
  }

  loadAppointments(page: number): void {
    this.appointmentService.findAll(page, 10).subscribe((data) => {
      this.pageInfo = data;
      this.appointments$ = of(data.content);
    });
  }

  goToPage(page: number): void {
    this.loadAppointments(page);
  }

  onViewAppointment(appointment: IAppointment) {}

  onEditAppointment(appointment: IAppointment) {}

  onCancelAppointment(appointment: IAppointment) {}
}
