import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/service/auth.service';
import { IResetPassword } from '../../../model/auth/reset-password.model';
import { getPasswordErrors, ValidatorsUtil } from '../../../shared/utils/validator-utils';

@Component({
  selector: 'app-reset-password',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss',
})
export class ResetPasswordComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  passwordForm: FormGroup;
  formSubmitted = false;

  response: string = '';
  isSuccess: boolean = false;
  token: string = '';

  constructor(private fb: FormBuilder) {
    this.passwordForm = this.fb.nonNullable.group({
      newPassword: ['', ValidatorsUtil.passwordValidator()],
      confirmPassword: [''],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.token = params['token'];
    });
  }

  get field() {
    return this.passwordForm.controls;
  }

  passwordErrors(): string[] {
    return getPasswordErrors(this.passwordForm.get('newPassword'));
  }

  onSubmit() {
    this.formSubmitted = true;
    this.isSuccess = false;
    this.response = '';

    if (this.passwordForm.invalid) return;
    if (!this.isPasswordConfirmationValid()) return;

    const body: IResetPassword = {
      token: this.token,
      password: this.passwordForm.value['newPassword'],
    };

    this.authService.resetPassword(body).subscribe({
      next: () => this.handleSuccess(),
    });
  }

  private handleSuccess(): void {
    this.isSuccess = true;
    this.response = 'Senha alterada com sucesso, redirecionando para login.';
    setTimeout(() => this.router.navigate(['/login']), 3000);
  }

  private isPasswordConfirmationValid(): boolean {
    const { newPassword, confirmPassword } = this.passwordForm.value;
    const isValid = newPassword === confirmPassword;

    this.response = isValid ? '' : 'As senhas informadas são diferentes.';
    return isValid;
  }
}
