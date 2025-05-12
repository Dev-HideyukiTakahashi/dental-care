import { AppointmentStatus } from '../../../../model/enum/appointment-status.enum';

export const isActionDisabled = (status: AppointmentStatus): boolean => {
  return status === AppointmentStatus.CANCELED || status === AppointmentStatus.COMPLETED;
};

export const getTableColumns = () => ['Paciente', 'Dentista', 'Data', 'Hora', 'Status', 'Ações'];
