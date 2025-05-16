import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/service/auth.service';
import { DentistService } from '../../core/service/dentist.service';
import { IDentistChangePassword } from '../../model/dentist-change-password.modal';
import { getPasswordErrors, ValidatorsUtil } from '../../shared/utils/validator-utils';

@Component({
  selector: 'app-dentist-password-update',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dentist-password-update.component.html',
  styleUrl: './dentist-password-update.component.scss',
})
export class DentistPasswordUpdateComponent implements OnInit {
  private dentistService = inject(DentistService);
  private authService = inject(AuthService);
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
        username: [null, Validators.required],
        password: ['', Validators.required],
        newPassword: ['', ValidatorsUtil.passwordValidator()],
        confirmPassword: ['', Validators.required],
      },
      { validator: this.passwordMatchValidator },
    );
  }

  ngOnInit(): void {
    const dentistUsername = this.authService.getUsername();
    this.passwordForm.get('username')?.setValue(dentistUsername);
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

    const body: IDentistChangePassword = this.passwordForm.value;

    this.dentistService.changePassword(body).subscribe({
      next: () => {
        this.successMessage = 'Senha alterada com sucesso! Redirecionando para tela de login';
        setTimeout(() => this.authService.logout(), 2500);
      },
      error: (err: any) => this.handleChangePasswordError(err),
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

  private handleChangePasswordError(err: any): void {
    this.isSubmitting = false;

    const errorMsg = err?.error?.error;

    switch (errorMsg) {
      case 'The current password is incorrect.':
        this.errorMessage = 'Senha atual inválida';
        break;

      case 'New password and confirmation password do not match.':
        this.errorMessage = 'As senhas não coincidem';
        break;

      default:
        this.errorMessage = errorMsg || 'Erro ao alterar a senha. Tente novamente mais tarde.';
        break;
    }
  }
}
