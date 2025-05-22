import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ScheduleService } from '../../core/service/schedule.service';
import { IAbsence } from '../../model/absence.model';

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
export class AbsenceScheduleComponent implements OnInit {
  private readonly scheduleService = inject(ScheduleService);

  leaveForm: FormGroup;
  currentLeave: IAbsence | null = null;
  isOnLeave = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly snackBar: MatSnackBar
  ) {
    this.leaveForm = this.fb.group(
      {
        startDate: ['', Validators.required],
        endDate: ['', Validators.required],
      },
      { validator: this.dateRangeValidator }
    );
  }

  ngOnInit(): void {
    this.scheduleService.findSelfAbsence().subscribe({
      next: (absence) => {
        if (absence?.absenceStart && absence?.absenceEnd) {
          this.currentLeave = {
            absenceStart: new Date(absence.absenceStart),
            absenceEnd: new Date(absence.absenceEnd),
          };

          this.isOnLeave = true;
        }
      },
    });
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
    if (this.leaveForm.invalid) return;

    this.currentLeave = {
      absenceStart: this.leaveForm.value.startDate,
      absenceEnd: this.leaveForm.value.endDate,
    };

    this.isOnLeave = true;

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
