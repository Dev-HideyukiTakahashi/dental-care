import { AppointmentStatus } from '../../../../model/enum/appointment-status.enum';

export const isActionDisabled = (status: AppointmentStatus): boolean => {
  return status === AppointmentStatus.CANCELED || status === AppointmentStatus.COMPLETED;
};
