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
import { LoginData } from '../../../model/login.model';

@Component({
  selector: 'app-login',
  imports: [RouterModule, FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  registerForm: FormGroup;
  errorMessage: string | null = null;
  showResetPasswordModal = false;
  recoveryEmail: string = '';

  constructor(private fb: FormBuilder) {
    this.registerForm = this.fb.nonNullable.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit() {
    const userData: LoginData = this.registerForm.value;

    this.authService.login(userData).subscribe({
      next: (response: any) => {
        localStorage.setItem('access_token', response.access_token);

        // TODO navigate to admin/dentist/patient page
        this.router.navigate(['/']);
      },
      error: () => {
        this.errorMessage = 'Usuário ou senha inválido';
      },
    });
  }

  openResetPasswordModal() {
    this.showResetPasswordModal = true;
  }

  closeModal() {
    this.showResetPasswordModal = false;
  }

  sendEmail() {
    // TODO
    console.log(this.recoveryEmail);
  }
}
