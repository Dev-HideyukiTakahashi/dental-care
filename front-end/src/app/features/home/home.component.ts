import { CommonModule, formatDate } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { Observable, of } from 'rxjs';
import { AppointmentService } from '../../core/service/appointment.service';
import { AuthService } from '../../core/service/auth.service';
import { IAppointment } from '../../model/appointment.model';
import { AppointmentStatus } from '../../model/enum/appointment-status.enum';
import { UserRole } from '../../model/enum/user-role.enum';
import { Page } from '../../model/page.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { AppointmentStatusPipe } from '../../shared/pipes/appointment-status.pipe';
import { PhonePipe } from '../../shared/pipes/phone.pipe';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    AppointmentStatusPipe,
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
    FormsModule,
    PhonePipe,
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
  showAppointmentDetailsModal: boolean = false;
  selectedAppointment: IAppointment | null = null;
  AppointmentStatus = AppointmentStatus;

  constructor(private dialog: MatDialog) {
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

  // Show modal appointment details
  openAppointmentDetails(appointmentId: number) {
    this.appointmentService.findById(appointmentId).subscribe((appointment) => {
      this.selectedAppointment = appointment;
      this.showAppointmentDetailsModal = true;
    });
    this.showAppointmentDetailsModal = true;
  }

  closeModal() {
    this.showAppointmentDetailsModal = false;
  }

  onEditAppointment(appointment: IAppointment) {}

  // cancel with confirm-dialog customized
  onCancelAppointment(appointmentId: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: 'Tem certeza que deseja cancelar a consulta?',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.appointmentService.cancelAppointment(appointmentId).subscribe({
          next: () => this.loadAppointments(0),
          error: (err) => console.log(err),
        });
      }
    });
  }
}
