import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IDentist } from '../../../model/dentist.model';

@Component({
  selector: 'app-dentist-form-modal',
  templateUrl: './dentist-form-modal.component.html',
  styleUrls: ['./dentist-form-modal.component.scss'],
  standalone: true,
  imports: [FormsModule],
})
export class DentistFormModalComponent {
  @Input() dentist: IDentist | null = null;
  @Output() save = new EventEmitter<IDentist>();
  @Output() close = new EventEmitter<void>();

  formData: IDentist = {
    name: '',
    speciality: '',
    registrationNumber: '',
    email: '',
    phone: '',
    password: '',
  };

  isSubmitting = false;
  isEditMode = false;
  showPassword = false;

  ngOnInit(): void {
    if (this.dentist) {
      this.isEditMode = true;
      this.formData = { ...this.dentist };
      delete this.formData.password;
    }
  }

  onSubmit(): void {
    if (this.isSubmitting) return;
    this.isSubmitting = true;

    this.save.emit(this.formData);
    setTimeout(() => (this.isSubmitting = false), 5000);
  }

  onClose(): void {
    this.close.emit();
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
