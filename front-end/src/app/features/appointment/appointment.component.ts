import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AppointmentService } from '../../core/service/appointment.service';
import { AuthService } from '../../core/service/auth.service';
import { DentistService } from '../../core/service/dentist.service';
import { PatientService } from '../../core/service/patient.service';
import { IAppointmentCreate } from '../../model/appointment.model';
import { IDentistMin } from '../../model/dentist-min.model';
import { UserRole } from '../../model/enum/user-role.enum';
import { IPatientMin } from '../../model/patient-min.model';

@Component({
  selector: 'app-appointment',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
  ],
  templateUrl: './appointment.component.html',
  styleUrl: './appointment.component.scss',
})
export class AppointmentComponent implements OnInit {
  private readonly dentistService: DentistService = inject(DentistService);
  private readonly patientService: PatientService = inject(PatientService);
  private readonly appointmentService: AppointmentService = inject(AppointmentService);
  private readonly authService: AuthService = inject(AuthService);
  private readonly route = inject(Router);

  appointmentForm: FormGroup;
  dentists: IDentistMin[] = [];
  patients: IPatientMin[] = [];
  dentistLoading = false;
  patientLoading = false;
  selectedDentistScore: number | string = 'Não avaliado';
  errorMessage: string | null = null;
  isAdmin: boolean = false;
  selectedDate: Date | null = null;
  selectedTime: string = '';
  timeOptions: string[] = [];

  constructor(private readonly fb: FormBuilder, private readonly snackBar: MatSnackBar) {
    this.appointmentForm = this.fb.group({
      dentist: ['', Validators.required],
      date: [null, Validators.required],
      time: ['', Validators.required],
      description: ['', Validators.required],
      patient: [''],
    });
    this.generateTimeOptions();
  }

  ngOnInit(): void {
    this.loadDentists();
    this.isAdmin = this.authService.getRole() === UserRole.Admin;

    if (this.isAdmin) {
      this.loadPatients();
    } else {
      this.appointmentForm.removeControl('patient');
    }
  }
  generateTimeOptions(): void {
    for (let hour = 8; hour <= 19; hour++) {
      const minutes = hour === 19 ? [0] : [0, 30];
      minutes.forEach((minute) => {
        this.timeOptions.push(
          `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`,
        );
      });
    }
  }

  updateDateTime(): void {
    if (this.selectedDate && this.appointmentForm.get('time')?.value) {
      const [hours, minutes] = this.appointmentForm.get('time')?.value.split(':');
      const newDate = new Date(this.selectedDate);
      newDate.setHours(parseInt(hours), parseInt(minutes));

      const formattedDate = `${newDate.getFullYear()}-${(newDate.getMonth() + 1)
        .toString()
        .padStart(2, '0')}-${newDate.getDate().toString().padStart(2, '0')}T${hours}:${minutes}:00`;

      this.appointmentForm.get('date')?.setValue(formattedDate);
    }
  }

  onTimeSelect(): void {
    this.selectedTime = this.appointmentForm.get('time')?.value;
    this.updateDateTime();
  }

  loadDentists(): void {
    this.dentistLoading = true;
    this.dentistService.findAll(0, 50).subscribe({
      next: (dentists) => {
        this.dentists = dentists.content;
        this.dentistLoading = false;
      },
      error: () => {
        this.showError('Erro ao carregar dentistas');
        this.dentistLoading = false;
      },
    });
  }

  loadPatients(): void {
    this.patientLoading = true;
    this.patientService.findAll(0, 50).subscribe({
      next: (dentists) => {
        this.patients = dentists.content;
        this.patientLoading = false;
      },
      error: () => {
        this.showError('Erro ao carregar pacientes');
        this.patientLoading = false;
      },
    });
  }

  onDentistSelect(): void {
    const dentistId = Number(this.appointmentForm.get('dentist')?.value);

    const dentist = this.dentists.find((d) => d.id === dentistId);

    this.selectedDentistScore = dentist?.score ?? 'Não avaliado';
  }

  onSubmit(): void {
    if (this.appointmentForm.invalid) {
      return;
    }

    this.errorMessage = '';
    const formValue = this.appointmentForm.value;

    const appointmentData: IAppointmentCreate = {
      date: formValue.date,
      description: formValue.description,
      dentist: { id: formValue.dentist },
      status: 'SCHEDULED',
      patient: this.isAdmin ? { id: formValue.patient } : null,
    };

    this.dentistLoading = true;
    this.patientLoading = true;
    this.appointmentService.create(appointmentData).subscribe({
      next: () => {
        this.showSuccess('Consulta agendada com sucesso!');
        this.appointmentForm.reset();
        this.dentistLoading = false;
        this.patientLoading = false;
        this.route.navigate(['/']);
      },
      error: (error) => {
        this.handleError(error.error.error);
        this.showError('Erro ao agendar consulta');
        this.dentistLoading = false;
        this.patientLoading = false;
      },
    });
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

  private handleError(error: string) {
    if (error === 'Appointment time is outside of working hours.') {
      this.errorMessage = 'Fora do horário de expediente.';
    }
    if (error === 'An appointment already exists for this time slot.') {
      this.errorMessage = 'Já existe uma consulta para esse horário';
    }
    if (error === 'The time falls within another appointment slot.') {
      this.errorMessage = 'O horário selecionado conflita com outro agendamento existente.';
    }
    if (error === 'problemDetail.org.springframework.web.bind.MethodArgumentNotValidException') {
      this.errorMessage = 'Data ou horário inválido.';
    }
  }
}
