import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IAppointment } from '../../../model/appointment.model';
import { AppointmentStatusPipe } from '../../pipes/appointment-status.pipe';
import { PhonePipe } from '../../pipes/phone.pipe';

@Component({
  selector: 'app-appointment-details-modal',
  imports: [CommonModule, AppointmentStatusPipe, PhonePipe],
  templateUrl: './appointment-details-modal.component.html',
  styleUrl: './appointment-details-modal.component.scss',
})
export class AppointmentDetailsModalComponent {
  @Input() appointment!: IAppointment;
  @Output() close = new EventEmitter<void>();
}
