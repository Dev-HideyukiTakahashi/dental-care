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
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ScheduleService } from '../../core/service/schedule.service';
import { IAbsence } from '../../model/absence.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

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
  errorMessage: string = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly snackBar: MatSnackBar,
    private readonly dialog: MatDialog
  ) {
    this.leaveForm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
    });
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

  scheduleLeave() {
    if (this.leaveForm.invalid) return;

    this.currentLeave = {
      absenceStart: this.leaveForm.value.startDate,
      absenceEnd: this.leaveForm.value.endDate,
    };

    this.scheduleService.createAbsence(this.currentLeave).subscribe({
      next: (schedule) => {
        this.isOnLeave = true;
        this.errorMessage = '';
        this.snackBar.open('Afastamento agendado com sucesso!', 'Fechar', {
          duration: 3000,
          panelClass: ['success-snackbar'],
        });
      },
      error: (err) => {
        err.error.error ===
        'The dentist cannot be on leave during this period, as there is already an appointment scheduled.'
          ? (this.errorMessage = 'Existe uma consulta marcada nesse período.')
          : (this.errorMessage = 'Erro nos campos de data');
      },
    });
  }

  cancelLeave() {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: 'Tem certeza que deseja cancelar o afastamento?',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.scheduleService.deleteAbsence().subscribe({
          next: () => {
            this.currentLeave = null;
            this.isOnLeave = false;
            this.snackBar.open('Afastamento cancelado com sucesso!', 'Fechar', {
              duration: 3000,
              panelClass: ['success-snackbar'],
            });
          },
        });
      }
    });
  }
}
