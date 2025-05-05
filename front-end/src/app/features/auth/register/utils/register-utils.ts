import { AbstractControl, ValidatorFn, Validators } from '@angular/forms';

export function getPasswordErrors(control: AbstractControl | null): string[] {
  const errors: string[] = [];

  if (!control || !control.errors) return errors;

  if (control.errors['required']) {
    errors.push('Senha é obrigatória');
  }
  if (control.errors['minlength']) {
    errors.push('Mínimo de 6 caracteres');
  }
  if (control.errors['maxlength']) {
    errors.push('Máximo de 20 caracteres');
  }
  if (control.errors['pattern']) {
    const value = control.value || '';
    if (!/[A-Z]/.test(value)) {
      errors.push('Pelo menos 1 letra maiúscula');
    }
    if (!/[a-z]/.test(value)) {
      errors.push('Pelo menos 1 letra minúscula');
    }
    if (!/[0-9]/.test(value)) {
      errors.push('Pelo menos 1 número');
    }
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(value)) {
      errors.push('Pelo menos 1 caractere especial');
    }
  }

  return errors;
}

export class ValidatorsUtil {
  static phonePattern: RegExp = /^\(?\d{2}\)?\s?9?\d{4}-?\d{4}$/;

  static passwordValidators(): ValidatorFn[] {
    return [
      Validators.required,
      Validators.minLength(6),
      Validators.maxLength(20),
      Validators.pattern(/[A-Z]/),
      Validators.pattern(/[a-z]/),
      Validators.pattern(/[0-9]/),
      Validators.pattern(/[!@#$%^&*(),.?":{}|<>]/),
    ];
  }

  static phoneValidator(): ValidatorFn[] {
    return [
      Validators.required,
      Validators.pattern(ValidatorsUtil.phonePattern),
    ];
  }
}
