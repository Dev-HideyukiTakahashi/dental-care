import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable, of } from 'rxjs';
import { AppointmentService } from '../../core/service/appointment.service';
import { AuthService } from '../../core/service/auth.service';
import { IAppointment } from '../../model/appointment.model';
import { AppointmentStatus } from '../../model/enum/appointment-status.enum';
import { UserRole } from '../../model/enum/user-role.enum';
import { Page } from '../../model/page.model';
import { IUpdateAppointment } from '../../model/update-appointment-model';
import { AppointmentDetailsModalComponent } from '../../shared/components/appointment-details-modal/appointment-details-modal.component';
import { AppointmentFilterComponent } from '../../shared/components/appointment-filter/appointment-filter.component';
import { AppointmentTableComponent } from '../../shared/components/appointment-table/appointment-table.component';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { EditAppointmentModalComponent } from '../../shared/components/edit-appointment-modal/edit-appointment-modal.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { getAppointmentErrorMessage } from './util/appointment-error.util';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    FormsModule,
    AppointmentDetailsModalComponent,
    AppointmentFilterComponent,
    AppointmentTableComponent,
    PaginationComponent,
    EditAppointmentModalComponent,
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
  showAppointmentDetailsModal: boolean = false;
  selectedAppointment: IAppointment | null = null;
  AppointmentStatus = AppointmentStatus;

  constructor(private dialog: MatDialog) {
    this.loadAppointments(0);
  }

  loadAppointments(page: number): void {
    this.appointmentService.findAll(page, 10).subscribe((response) => {
      this.pageInfo = response;
      this.appointments$ = of(response.content);
    });
  }

  get role(): UserRole | null {
    return this.authService.getRole();
  }

  onFilterByDate(date: Date | null): void {
    if (!date) return;

    const dateStr = date.toISOString().split('T')[0];
    this.appointmentService.findByDate(this.pageInfo.page.number, 10, dateStr).subscribe({
      next: (response) => {
        this.pageInfo = response;
        this.appointments$ = of(response.content);
      },
      error: (err) => console.error(err),
    });
  }

  openAppointmentDetails(appointmentId: number) {
    this.appointmentService.findById(appointmentId).subscribe((appointment) => {
      this.selectedAppointment = appointment;
      this.showAppointmentDetailsModal = true;
    });
    this.showAppointmentDetailsModal = true;
  }

  closeDetailsModal() {
    this.showAppointmentDetailsModal = false;
  }

  showEditModal = false;
  editedAppointment!: IAppointment;
  editMessage: string | null = null;
  editSuccess: boolean = false;

  openEditModal(appointment: IAppointment): void {
    this.editedAppointment = appointment;
    this.showEditModal = true;
    this.editMessage = null;
    this.editSuccess = false;
  }

  closeEditModal() {
    this.showEditModal = false;
  }

  updateAppointment(updateData: IUpdateAppointment) {
    const body: IUpdateAppointment = {
      date: updateData.date,
    };
    this.appointmentService.updateAppointment(this.editedAppointment.id!, body).subscribe({
      next: () => this.handleEditSuccess(),
      error: (err) => (this.editMessage = getAppointmentErrorMessage(err)),
    });
  }

  private handleEditSuccess(): void {
    this.editMessage = 'Consulta atualizada com sucesso!';
    this.editSuccess = true;
    this.loadAppointments(0);
    setTimeout(() => {
      this.editMessage = null;
      this.closeEditModal();
    }, 3000);
  }

  //  ************************************************
  //  ******** Cancel Appointment ********************
  //  ************************************************
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
