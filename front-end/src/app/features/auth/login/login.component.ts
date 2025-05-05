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
import { AuthService } from '../../../core/auth.service';
import { LoginData } from '../../../shared/model/login.model';

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

        const role = this.authService.getUserRole();
        localStorage.setItem('role', role);

        // TODO retirar após tests
        console.log('Usuário logado');

        this.router.navigate(['/home']);
      },
      error: () => {
        this.errorMessage = 'Usuário ou senha inválido';
      },
    });
  }
}
