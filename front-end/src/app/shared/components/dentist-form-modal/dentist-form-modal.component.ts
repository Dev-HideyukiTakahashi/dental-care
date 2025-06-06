import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { IDentist } from '../../../model/dentist.model';
import { getPasswordErrors, ValidatorsUtil } from '../../utils/validator-utils';

@Component({
  selector: 'app-dentist-form-modal',
  templateUrl: './dentist-form-modal.component.html',
  styleUrls: ['./dentist-form-modal.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
})
export class DentistFormModalComponent {
  @Input() dentist: IDentist | null = null;
  @Input() formErrorMessage: string | null = null;
  @Output() save = new EventEmitter<IDentist>();
  @Output() close = new EventEmitter<void>();

  registerForm: FormGroup;

  isSubmitting = false;
  isEditMode = false;
  showPassword = false;

  constructor(private fb: FormBuilder) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      speciality: ['', Validators.required],
      registrationNumber: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', ValidatorsUtil.phoneValidator()],
      password: ['', ValidatorsUtil.passwordValidator()],
    });
  }

  ngOnInit(): void {
    if (this.dentist) {
      this.isEditMode = true;

      this.registerForm.patchValue({
        ...this.dentist,
        phone: this.formatPhone(this.dentist.phone),
        password: '',
      });

      this.registerForm.get('password')?.clearValidators(); // remove validators in password in edit
      this.registerForm.get('password')?.updateValueAndValidity(); // update state in validation
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid || this.isSubmitting) return;

    // Extract and clean data
    const userData = {
      ...this.registerForm.value,
      phone: ValidatorsUtil.cleanPhone(this.field['phone'].value),
    };

    this.isSubmitting = true;
    const formValue: IDentist = userData;

    // EDIT MODAL SEND SAME PASSWORD
    if (this.isEditMode && !formValue.password) {
      formValue.id = this.dentist?.id;
      delete formValue.password; // remove this attribute in payload
    }

    // CREATE MODAL
    this.save.emit(formValue);
    setTimeout(() => (this.isSubmitting = false), 2500);
  }

  onClose(): void {
    this.close.emit();
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  getPasswordErrors(): string[] {
    const control = this.registerForm.get('password');
    if (!control || (!control.touched && !control.dirty)) return [];
    return getPasswordErrors(control);
  }

  get field() {
    return this.registerForm.controls;
  }

  private formatPhone(phone: string): string {
    const digits = phone.replace(/\D/g, '');
    if (digits.length === 11) {
      return `(${digits.substring(0, 2)}) ${digits.substring(
        2,
        7
      )}-${digits.substring(7, 11)}`;
    }
    return phone;
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

    this.registerForm.get('phone')?.setValue(value, { emitEvent: false });
  }
}
