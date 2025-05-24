import { Component, inject } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';

// CHART IMPORTS
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Chart, registerables } from 'chart.js';
import { AppointmentService } from '../../core/service/appointment.service';
import { DentistService } from '../../core/service/dentist.service';
import { PatientService } from '../../core/service/patient.service';
import { IAppointment } from '../../model/appointment.model';
import {
  DashboardChartData,
  DashboardChartType,
} from '../../shared/utils/chart-types';
import {
  getDashboardChartConfig,
  initialChartData,
  transformToChartData,
} from '../../shared/utils/chart-utils';
Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  imports: [BaseChartDirective, CommonModule, MatIconModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  private readonly dentistService = inject(DentistService);
  private readonly patientService = inject(PatientService);
  private readonly appointmentService = inject(AppointmentService);

  public chartOptions = getDashboardChartConfig();
  public chartData: DashboardChartData = initialChartData;
  public chartType: DashboardChartType = 'bar';

  totalPatients = 0;
  totalAppointments = 0;
  totalDentists = 0;
  today: Date = new Date();

  todayAppointments: any[] = [];

  constructor(private readonly snackBar: MatSnackBar) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.loadDentists();
    this.loadPatients();
    this.loadAppointments();
    this.loadTodayAppointments();
  }

  loadTodayAppointments(): void {
    this.appointmentService
      .findByDate(0, 50, this.today.toISOString().split('T')[0])
      .subscribe({
        next: (page) => {
          this.todayAppointments = [...page.content].sort(
            (a: IAppointment, b: IAppointment) =>
              new Date(a.date).getTime() - new Date(b.date).getTime()
          );
        },
        error: (error) => {
          console.error('Erro ao carregar agendamentos de hoje:', error);
        },
      });
  }

  completeAppointment(appointmentId: number): void {
    console.log('complete');
    this.appointmentService.completeAppointment(appointmentId).subscribe({
      next: () => {
        this.loadTodayAppointments();
        this.snackBar.open('Consulta concluída com sucesso!', 'Fechar', {
          duration: 3000,
          panelClass: ['success-snackbar'],
        });
      },
    });
  }

  private loadDentists() {
    this.dentistService.getDentistsWithRatings().subscribe({
      next: (dentists) => {
        this.totalDentists = dentists.page.totalElements;
        this.chartData = transformToChartData(dentists.content);
      },
    });
  }

  private loadPatients() {
    this.patientService.findAll(0, 10).subscribe({
      next: (patients) => {
        this.totalPatients = patients.page.totalElements;
      },
    });
  }

  private loadAppointments() {
    this.appointmentService.findAll(0, 10).subscribe({
      next: (appointments) => {
        this.totalAppointments = appointments.page.totalElements;
      },
    });
  }
}
