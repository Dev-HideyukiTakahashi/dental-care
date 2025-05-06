import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../../../core/service/auth.service';
import { getPasswordErrors, ValidatorsUtil } from './utils/register-utils';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterModule, CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup;
  formSubmitted = false;

  // Feedback to user
  apiResponse: string = '';
  isSuccess: boolean = false;

  constructor(private fb: FormBuilder) {
    this.registerForm = this.fb.nonNullable.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', ValidatorsUtil.phoneValidator()],
      password: ['', ValidatorsUtil.passwordValidator()],
    });
  }

  get field() {
    return this.registerForm.controls;
  }

  passwordErrors(): string[] {
    return getPasswordErrors(this.registerForm.get('password'));
  }

  onSubmit() {
    this.formSubmitted = true;

    if (this.registerForm.invalid) return;

    // Extract and clean data
    const userData = {
      ...this.registerForm.value,
      phone: ValidatorsUtil.cleanPhone(this.field['phone'].value),
    };

    // Reset feedback state
    this.apiResponse = '';
    this.isSuccess = false;

    this.authService.signup(userData).subscribe({
      next: () => this.handleSuccess(),
      error: (err) => this.handleError(err),
    });
  }

  private handleSuccess(): void {
    this.apiResponse = 'Cadastro realizado com sucesso';
    this.isSuccess = true;
    setTimeout(() => this.router.navigate(['/login']), 3000);
  }

  private handleError(err: any): void {
    if (err.error?.error === 'Email already registered.') {
      this.apiResponse = 'Email já cadastrado.';
    }
  }
}
