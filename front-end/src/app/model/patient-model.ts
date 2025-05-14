import { IAppointment } from './appointment.model';
import { IRole } from './role.model';

export interface IPatient {
  id: number;
  medicalHistory: string;
  name: string;
  email: string;
  password: string;
  phone: string;
  roles: IRole[];
  appointments: IAppointment[];
}
