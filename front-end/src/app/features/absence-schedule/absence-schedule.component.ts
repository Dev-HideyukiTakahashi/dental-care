import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { DateAdapter, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

interface LeavePeriod {
  start: Date;
  end: Date;
}

@Component({
  selector: 'app-absence-schedule',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatInputModule,
    MatFormFieldModule,
    MatNativeDateModule,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule,
  ],
  templateUrl: './absence-schedule.component.html',
  styleUrl: './absence-schedule.component.scss',
})
export class AbsenceScheduleComponent {
  leaveForm: FormGroup;
  currentLeave: LeavePeriod | null = null;
  isOnLeave = false;

  constructor(
    private fb: FormBuilder,
    private dateAdapter: DateAdapter<Date>,
    private snackBar: MatSnackBar
  ) {
    this.dateAdapter.setLocale('pt-BR');

    this.leaveForm = this.fb.group(
      {
        startDate: ['', Validators.required],
        endDate: ['', Validators.required],
      },
      { validator: this.dateRangeValidator }
    );
  }

  dateRangeValidator(form: FormGroup) {
    const start = form.get('startDate')?.value;
    const end = form.get('endDate')?.value;

    if (start && end && start > end) {
      return { endBeforeStart: true };
    }
    return null;
  }

  scheduleLeave() {
    if (this.leaveForm.invalid) {
      return;
    }

    this.currentLeave = {
      start: this.leaveForm.value.startDate,
      end: this.leaveForm.value.endDate,
    };

    this.isOnLeave = true;
    this.leaveForm.reset();

    this.snackBar.open('Afastamento agendado com sucesso!', 'Fechar', {
      duration: 3000,
      panelClass: ['success-snackbar'],
    });
  }

  cancelLeave() {
    this.currentLeave = null;
    this.isOnLeave = false;

    this.snackBar.open('Afastamento cancelado com sucesso!', 'Fechar', {
      duration: 3000,
      panelClass: ['success-snackbar'],
    });
  }
}
