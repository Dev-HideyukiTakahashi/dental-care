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
import { LoginData } from '../../../model/auth/login.model';
import { IRecoverToken } from '../../../model/auth/recover-token.model';

@Component({
  selector: 'app-login',
  imports: [RouterModule, FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  loginForm: FormGroup;
  errorMessage: string | null = null;

  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.nonNullable.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit() {
    const userData: LoginData = this.loginForm.value;

    this.authService.login(userData).subscribe({
      next: (response: any) => {
        localStorage.setItem('access_token', response.access_token);

        this.router.navigate(['/']);
      },
      error: () => {
        this.errorMessage = 'Usuário ou senha inválido';
      },
    });
  }

  // ***** MODAL RESET PASSWORD *****

  showResetPasswordModal = false;
  recoveryToken: IRecoverToken = {} as IRecoverToken;
  successModal: string = '';
  errorModal: string = '';
  isSending: boolean = false;

  openResetPasswordModal() {
    this.errorModal = '';
    this.successModal = '';
    this.recoveryToken.email = '';
    this.showResetPasswordModal = true;
  }

  closeModal() {
    this.showResetPasswordModal = false;
  }

  sendEmail() {
    this.isSending = true;
    this.authService.recoverToken(this.recoveryToken).subscribe({
      next: (response: any) => {
        this.successModal = 'E-mail enviado com sucesso!';
        this.errorModal = '';
      },
      error: () => {
        this.successModal = '';
        this.errorModal = 'Email inválido';
        this.isSending = false;
      },
      complete: () => {
        setTimeout(() => {
          this.closeModal();
          this.successModal = '';
          this.isSending = false;
        }, 3000);
      },
    });
  }
}
