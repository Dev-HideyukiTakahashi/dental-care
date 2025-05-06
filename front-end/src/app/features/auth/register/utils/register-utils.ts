import { AbstractControl, ValidatorFn, Validators } from '@angular/forms';

/**
 * Returns an array of password error messages based on the control's validation errors.
 * Only the first matched error is returned to avoid flooding the UI with multiple messages.
 */
export function getPasswordErrors(control: AbstractControl | null): string[] {
  if (!control || !control.errors) return [];

  if (control.errors['required']) return ['Senha é obrigatória'];
  if (control.errors['minlength']) return ['Mínimo de 6 caracteres'];
  if (control.errors['maxlength']) return ['Máximo de 20 caracteres'];
  if (control.errors['uppercase']) return ['Pelo menos 1 letra maiúscula'];
  if (control.errors['lowercase']) return ['Pelo menos 1 letra minúscula'];
  if (control.errors['number']) return ['Pelo menos 1 número'];
  if (control.errors['special']) return ['Pelo menos 1 caractere especial'];

  return [];
}

/**
 * Utility class containing reusable validators and helper functions
 */
export class ValidatorsUtil {
  // Phone pattern that matches Brazilian phone format (e.g., (11) 91234-5678)
  static readonly phonePattern: RegExp = /^\(?\d{2}\)?[\s-]?[9]{1}\d{4}[-]?\d{4}$/;

  /**
   * Removes all non-numeric characters from a phone number string.
   * @param phone Raw phone string input.
   * @returns Cleaned numeric-only phone string.
   */
  static cleanPhone(phone: string): string {
    return phone.replace(/\D/g, '');
  }

  /**
   * Returns an array of validators to validate a Brazilian phone number.
   * @returns Validators for required and phone pattern.
   */
  static phoneValidator(): ValidatorFn[] {
    return [Validators.required, Validators.pattern(ValidatorsUtil.phonePattern)];
  }

  /**
   * Custom validator function for passwords.
   * Validates minimum and maximum length, and the presence of uppercase, lowercase,
   * numeric and special characters.
   * @returns Object with validation errors or null if valid.
   */
  static passwordValidator(): ValidatorFn {
    return (control: AbstractControl) => {
      const value = control.value || '';
      const errors: { [key: string]: boolean } = {};

      if (!value) {
        errors['required'] = true;
      } else {
        if (value.length < 6) errors['minlength'] = true;
        if (value.length > 20) errors['maxlength'] = true;
        if (!/[A-Z]/.test(value)) errors['uppercase'] = true;
        if (!/[a-z]/.test(value)) errors['lowercase'] = true;
        if (!/[0-9]/.test(value)) errors['number'] = true;
        if (!/[!@#$%^&*(),.?":{}|<>]/.test(value)) errors['special'] = true;
      }

      return Object.keys(errors).length > 0 ? errors : null;
    };
  }
}
