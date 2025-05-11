import { Pipe, PipeTransform } from '@angular/core';
import { AppointmentStatus } from '../../model/appointment-status.enum';

@Pipe({
  name: 'appointmentStatus',
})
export class AppointmentStatusPipe implements PipeTransform {
  transform(status: AppointmentStatus): string {
    switch (status) {
      case AppointmentStatus.SCHEDULED:
        return 'Agendado';
      case AppointmentStatus.CANCELED:
        return 'Cancelado';
      case AppointmentStatus.COMPLETED:
        return 'Concluído';
      default:
        return status;
    }
  }
}
