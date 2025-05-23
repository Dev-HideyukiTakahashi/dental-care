import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { IPatient } from '../../../model/patient-model';
import { getPasswordErrors, ValidatorsUtil } from '../../utils/validator-utils';

@Component({
  selector: 'app-patient-form-modal',
  templateUrl: './patient-form-modal.component.html',
  styleUrls: ['./patient-form-modal.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
})
export class PatientFormModalComponent {
  @Input() patient: IPatient | null = null;
  @Input() formErrorMessage: string | null = null;
  @Output() save = new EventEmitter<IPatient>();
  @Output() close = new EventEmitter<void>();

  registerForm: FormGroup;

  isSubmitting = false;
  isEditMode = false;
  showPassword = false;

  constructor(private fb: FormBuilder) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      medicalHistory: [''],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', ValidatorsUtil.phoneValidator()],
      password: ['', ValidatorsUtil.passwordValidator()],
    });
  }

  ngOnInit(): void {
    if (this.patient) {
      this.isEditMode = true;
      this.registerForm.patchValue({
        ...this.patient,
        password: '',
      });

      this.registerForm.get('password')?.clearValidators();
      this.registerForm.get('password')?.updateValueAndValidity();
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid || this.isSubmitting) return;

    const patientData = {
      ...this.registerForm.value,
      phone: ValidatorsUtil.cleanPhone(this.field['phone'].value),
    };

    this.isSubmitting = true;
    const formValue: IPatient = patientData;

    if (this.isEditMode && !formValue.password) {
      formValue.id = this.patient?.id;
      delete formValue.password; // remove this attribute in payload
    }

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
