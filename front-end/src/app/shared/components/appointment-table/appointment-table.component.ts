import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { IAppointment } from '../../../model/appointment.model';
import { AppointmentStatus } from '../../../model/enum/appointment-status.enum';
import { UserRole } from '../../../model/enum/user-role.enum';
import { AppointmentStatusPipe } from '../../../shared/pipes/appointment-status.pipe';
import { isActionDisabled } from './util/appointment-table.utils';

@Component({
  selector: 'app-appointment-table',
  standalone: true,
  imports: [CommonModule, MatButtonModule, AppointmentStatusPipe],
  templateUrl: './appointment-table.component.html',
  styleUrls: ['./appointment-table.component.scss'],
})
export class AppointmentTableComponent {
  @Input() appointments: IAppointment[] = [];
  @Input() role?: UserRole | null;
  @Output() viewDetails = new EventEmitter<number>();
  @Output() editAppointment = new EventEmitter<IAppointment>();
  @Output() cancelAppointment = new EventEmitter<number>();
  @Output() completeAppointment = new EventEmitter<number>();

  AppointmentStatus = AppointmentStatus;

  isActionDisabled = isActionDisabled;
}
