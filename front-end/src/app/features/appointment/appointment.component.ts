import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
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
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './appointment.component.html',
  styleUrl: './appointment.component.scss',
})
export class AppointmentComponent implements OnInit {
  private readonly dentistService: DentistService = inject(DentistService);
  private readonly patientService: PatientService = inject(PatientService);
  private readonly appointmentService: AppointmentService =
    inject(AppointmentService);
  private readonly authService: AuthService = inject(AuthService);

  appointmentForm: FormGroup;
  dentists: IDentistMin[] = [];
  patients: IPatientMin[] = [];
  dentistLoading = false;
  patientLoading = false;
  selectedDentistScore: number | string = 'Não avaliado';
  errorMessage: string | null = null;
  isAdmin: boolean = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly snackBar: MatSnackBar
  ) {
    this.appointmentForm = this.fb.group({
      dentist: ['', Validators.required],
      date: ['', Validators.required],
      description: ['', Validators.required],
      patient: [''],
    });
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
      this.errorMessage =
        'O horário selecionado conflita com outro agendamento existente.';
    }
    if (
      error ===
      'problemDetail.org.springframework.web.bind.MethodArgumentNotValidException'
    ) {
      this.errorMessage = 'Data ou horário inválido.';
    }
  }
}
