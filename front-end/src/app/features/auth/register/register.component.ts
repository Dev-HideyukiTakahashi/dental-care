import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterModule } from '@angular/router';

import { IRegisterPatient } from '../../../shared/model/register-patient.interface';
import { getPasswordErrors, ValidatorsUtil } from './utils/register-utils';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterModule, CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  userData: IRegisterPatient = {} as IRegisterPatient;

  registerForm: FormGroup;
  formSubmitted = false;

  constructor(private fb: FormBuilder) {
    this.registerForm = this.fb.nonNullable.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', ValidatorsUtil.phoneValidator()],
      password: ['', ValidatorsUtil.passwordValidators()],
    });
  }
  get field() {
    return this.registerForm.controls;
  }

  onSubmit() {
    this.formSubmitted = true;

    if (this.registerForm.invalid) {
      return;
    }

    const formData = this.registerForm.value;
    console.log('Dados válidos:', formData);

    // TODO backend endpoint login
  }

  passwordErrors(): string[] {
    return getPasswordErrors(this.registerForm.get('password'));
  }
}
