import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTimepickerModule } from '@angular/material/timepicker';
import { IAppointment } from '../../../model/appointment.model';
import { IUpdateAppointment } from '../../../model/update-appointment-model';
import { formatDate } from '../../utils/format-date-utils';

@Component({
  selector: 'app-edit-appointment-modal',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatTimepickerModule,
    MatDatepickerModule,
    FormsModule,
  ],
  templateUrl: './edit-appointment-modal.component.html',
  styleUrls: ['./edit-appointment-modal.component.scss'],
})
export class EditAppointmentModalComponent {
  @Input() appointment!: IAppointment;
  @Input() message: string | null = null;
  @Input() isSuccess = false;
  @Output() save = new EventEmitter<IUpdateAppointment>();
  @Output() close = new EventEmitter<void>();

  value!: Date;

  onSubmit(): void {
    const formattedDate = formatDate(this.value);

    const updateData: IUpdateAppointment = {
      date: formattedDate,
    };
    this.save.emit(updateData);
  }

  onClose(): void {
    this.close.emit();
  }

  filteredByDate() {}
}
