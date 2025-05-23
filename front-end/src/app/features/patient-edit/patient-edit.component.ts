import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterModule } from '@angular/router';
import { PatientService } from '../../core/service/patient.service';
import { IPatient } from '../../model/patient-model';
import { ValidatorsUtil } from '../../shared/utils/validator-utils';
@Component({
  selector: 'app-patient-edit',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './patient-edit.component.html',
  styleUrl: './patient-edit.component.scss',
})
export class PatientEditComponent implements OnInit {
  editForm: FormGroup;
  isLoading = true;

  constructor(
    private fb: FormBuilder,
    private patientService: PatientService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.editForm = this.fb.group({
      id: [''],
      medicalHistory: ['', Validators.required],
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [
        '',
        [Validators.required, Validators.pattern(/^\(\d{2}\) \d{5}-\d{4}$/)],
      ],
    });
  }

  ngOnInit(): void {
    this.loadPatientData();
  }

  loadPatientData(): void {
    this.patientService.getMe().subscribe({
      next: (patient: IPatient) => {
        this.editForm.patchValue({
          id: patient.id,
          medicalHistory: patient.medicalHistory,
          name: patient.name,
          email: patient.email,
          phone: patient.phone,
        });
        this.isLoading = false;
      },
      error: () => {
        this.snackBar.open('Erro ao carregar dados do paciente', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar'],
        });
        this.router.navigate(['/patient/profile']);
      },
    });
  }

  formatPhoneNumber(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');

    if (value.length > 0) {
      value = `(${value.substring(0, 2)}) ${value.substring(
        2,
        7
      )}-${value.substring(7, 11)}`;
    }

    this.editForm.get('phone')?.setValue(value, { emitEvent: false });
  }

  onSubmit(): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    const updatedPatient: IPatient = {
      id: this.editForm.value.id,
      medicalHistory: this.editForm.value.medicalHistory,
      name: this.editForm.value.name,
      email: this.editForm.value.email,
      phone: ValidatorsUtil.cleanPhone(this.editForm.value.phone),
    };

    this.patientService.updatePatient(updatedPatient).subscribe({
      next: () => {
        this.snackBar.open('Dados atualizados com sucesso!', 'Fechar', {
          duration: 3000,
          panelClass: ['success-snackbar'],
        });
        this.router.navigate(['/']);
      },
      error: () => {
        this.snackBar.open('Erro ao atualizar dados', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar'],
        });
      },
    });
  }
}
