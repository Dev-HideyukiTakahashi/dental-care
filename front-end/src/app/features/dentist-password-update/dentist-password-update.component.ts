import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DentistService } from '../../core/service/dentist.service';
import { getPasswordErrors, ValidatorsUtil } from '../../shared/utils/validator-utils';

@Component({
  selector: 'app-dentist-password-update',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dentist-password-update.component.html',
  styleUrl: './dentist-password-update.component.scss',
})
export class DentistPasswordUpdateComponent {
  private dentistService = inject(DentistService);
  private router = inject(Router);

  passwordForm: FormGroup;
  isSubmitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  showCurrentPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  constructor(private fb: FormBuilder) {
    this.passwordForm = this.fb.group(
      {
        currentPassword: ['', Validators.required],
        newPassword: ['', ValidatorsUtil.passwordValidator()],
        confirmPassword: ['', Validators.required],
      },
      { validator: this.passwordMatchValidator },
    );
  }

  passwordMatchValidator(form: FormGroup) {
    return form.get('newPassword')?.value === form.get('confirmPassword')?.value
      ? null
      : { mismatch: true };
  }

  onSubmit() {
    if (this.passwordForm.invalid || this.isSubmitting) return;

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const { currentPassword, newPassword } = this.passwordForm.value;

    this.dentistService.changePassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.successMessage = 'Senha alterada com sucesso!';
        setTimeout(() => this.router.navigate(['/perfil']), 2000);
      },
      error: (err: any) => {
        this.isSubmitting = false;
        this.errorMessage =
          err.error?.message || 'Erro ao alterar senha. Verifique os dados e tente novamente.';
      },
    });
  }

  onCancel() {
    this.router.navigate(['/perfil']);
  }

  toggleCurrentPasswordVisibility() {
    this.showCurrentPassword = !this.showCurrentPassword;
  }

  toggleNewPasswordVisibility() {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  getPasswordErrors(field: string): string[] {
    const control = this.passwordForm.get(field);
    if (!control || (!control.touched && !control.dirty)) return [];
    return getPasswordErrors(control);
  }
}
