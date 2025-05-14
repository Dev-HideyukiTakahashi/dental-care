import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IPatientMin } from '../../../model/patient-min.model';
import { PhonePipe } from '../../pipes/phone.pipe';

@Component({
  selector: 'app-patient-table',
  imports: [CommonModule, PhonePipe],
  templateUrl: './patient-table.component.html',
  styleUrl: './patient-table.component.scss',
})
export class PatientTableComponent {
  @Input() patients: IPatientMin[] = [];
  @Output() viewDetails = new EventEmitter<number>();
  @Output() editPatient = new EventEmitter<number>();
  @Output() deletePatient = new EventEmitter<number>();
}
