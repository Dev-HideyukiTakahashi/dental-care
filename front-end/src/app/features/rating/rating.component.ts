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
import { IRating } from '../../model/rating.model';

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

  // tempRatings: { [key: number]: number } = {};
  // tempComments: { [key: number]: string } = {};
  tempRating: Partial<IRating> = {
    score: undefined,
    comment: '',
    appointmentId: undefined,
  };

  constructor(
    private appointmentService: AppointmentService,
    private ratingService: RatingService,
    private snackBar: MatSnackBar
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
      (a) =>
        a.status === AppointmentStatus.COMPLETED &&
        a.rating !== null &&
        a.rating?.rated
    );
  }

  buildNotRatedAppointments(appointments: IAppointment[]) {
    this.notRatedAppointments = appointments.filter(
      (a) => a.status === AppointmentStatus.COMPLETED && a.rating !== null
    );
    console.log(appointments);
  }

  setRating(appointment: any, rating: number): void {
    this.tempRatings[appointment.id] = rating;
  }

  submitRating(appointment: any): void {
    const ratingData: IRating = {
      score: this.tempRatings[appointment.id],
      comment: this.tempComments[appointment.id] || '',
      patientId: appointment.patientId,
      dentistId: appointment.dentistId,
      appointmentId: appointment.id,
    };

    this.ratingService.submitRating(ratingData).subscribe({
      next: (response) => {
        this.snackBar.open('Avaliação enviada com sucesso!', 'Fechar', {
          duration: 3000,
          panelClass: ['success-snackbar'],
        });
        this.loadAppointments();
      },
      error: () => {
        this.snackBar.open('Erro ao enviar avaliação', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar'],
        });
      },
    });
  }
}
