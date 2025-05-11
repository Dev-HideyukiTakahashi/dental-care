import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'phone',
})
export class PhonePipe implements PipeTransform {
  transform(phone: string): string {
    if (!phone) return '';

    if (phone.length === 11) {
      const ddd = phone.slice(0, 2);
      const part1 = phone.slice(2, 7);
      const part2 = phone.slice(7);
      return `(${ddd}) ${part1}-${part2}`;
    }

    if (phone.length === 10) {
      const ddd = phone.slice(0, 2);
      const part1 = phone.slice(2, 6);
      const part2 = phone.slice(6);
      return `(${ddd}) ${part1}-${part2}`;
    }

    return phone;
  }
}
