import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AppointmentService } from '../../core/service/appointment.service';
import { RatingService } from '../../core/service/rating.service';
import { IAppointment } from '../../model/appointment.model';
import { AppointmentStatus } from '../../model/enum/appointment-status.enum';
import { IRatingCreate } from '../../model/rating.model';

@Component({
  selector: 'app-rating',
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
  ],
  templateUrl: './rating.component.html',
  styleUrl: './rating.component.scss',
})
export class RatingsComponent {
  ratedAppointments: IAppointment[] = [];
  notRatedAppointments: IAppointment[] = [];

  tempRatings: { [appointmentId: number]: Partial<IRatingCreate> } = {};

  constructor(
    private readonly appointmentService: AppointmentService,
    private readonly ratingService: RatingService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.appointmentService.findAll(0, 20).subscribe({
      next: (appointments) => {
        this.buildRatedAppointments(appointments.content);

        this.buildNotRatedAppointments(appointments.content);
      },
      error: () => {
        this.snackBar.open('Erro ao carregar consultas', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar'],
        });
      },
    });
  }

  buildRatedAppointments(appointments: IAppointment[]) {
    this.ratedAppointments = appointments.filter(
      (a) => a.status === AppointmentStatus.COMPLETED && a.rating
    );
  }

  buildNotRatedAppointments(appointments: IAppointment[]) {
    this.notRatedAppointments = appointments.filter(
      (a) => a.status === AppointmentStatus.COMPLETED && !a.rating
    );
  }

  setRating(appointment: IAppointment, score: number): void {
    this.tempRatings[appointment.id] = {
      ...(this.tempRatings[appointment.id] || {}),
      score: score,
      appointmentId: appointment.id,
      patientId: appointment.patient.id,
      dentistId: appointment.dentist.id,
    };
  }

  submitRating(appointment: IAppointment): void {
    const tempRating = this.tempRatings[appointment.id];

    if (!tempRating?.score || !tempRating.appointmentId) {
      this.showError('Por favor, selecione uma avaliação');
      return;
    }

    const ratingData: IRatingCreate = {
      score: tempRating.score,
      comment: tempRating.comment || '',
      patientId: appointment.patient.id,
      dentistId: appointment.dentist.id,
      appointmentId: appointment.id,
    };

    this.ratingService.submitRating(ratingData).subscribe({
      next: () => {
        this.showSuccess('Avaliação enviada com sucesso!');
        this.resetTempRating(appointment.id);
        this.loadAppointments();
      },
      error: () => {
        this.showError('Erro ao enviar avaliação');
      },
    });
  }

  updateComment(appointment: IAppointment, comment: string): void {
    this.tempRatings[appointment.id] = {
      ...(this.tempRatings[appointment.id] || {}),
      comment: comment,
      appointmentId: appointment.id,
      patientId: appointment.patient.id,
      dentistId: appointment.dentist.id,
    };
  }

  private resetTempRating(appointmentId: number): void {
    delete this.tempRatings[appointmentId];
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
      panelClass: ['success-snackbar'],
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
      panelClass: ['error-snackbar'],
    });
  }
}
