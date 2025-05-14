import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IPatient } from '../../../model/patient-model';
import { AppointmentStatusPipe } from '../../pipes/appointment-status.pipe';
import { PhonePipe } from '../../pipes/phone.pipe';

@Component({
  selector: 'app-patient-details',
  imports: [CommonModule, PhonePipe, AppointmentStatusPipe],
  templateUrl: './patient-details.component.html',
  styleUrl: './patient-details.component.scss',
})
export class PatientDetailsComponent {
  @Input() patient!: IPatient;
  @Output() close = new EventEmitter<void>();
}
