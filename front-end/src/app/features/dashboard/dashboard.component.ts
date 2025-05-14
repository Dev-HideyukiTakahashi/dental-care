import { Component, inject } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';

// CHART IMPORTS
import { Chart, registerables } from 'chart.js';
import { AppointmentService } from '../../core/service/appointment.service';
import { DentistService } from '../../core/service/dentist.service';
import { PatientService } from '../../core/service/patient.service';
import { DashboardChartData, DashboardChartType } from '../../shared/utils/chart-types';
import {
  getDashboardChartConfig,
  initialChartData,
  transformToChartData,
} from '../../shared/utils/chart-utils';
Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  imports: [BaseChartDirective],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  private dentistService = inject(DentistService);
  private patientService = inject(PatientService);
  private appointmentService = inject(AppointmentService);

  public chartOptions = getDashboardChartConfig();
  public chartData: DashboardChartData = initialChartData;
  public chartType: DashboardChartType = 'bar';

  totalPatients = 0;
  totalAppointments = 0;
  totalDentists = 0;

  constructor() {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.loadDentists();
    this.loadPatients();
    this.loadAppointments();
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
