import { IDentistMin } from './dentist-min.model';
import { AppointmentStatus } from './enum/appointment-status.enum';
import { IPatientMin } from './patient-min.model';
import { IRating } from './rating.model';

export interface IAppointment {
  id: number;
  date: string;
  status: AppointmentStatus;
  description: string;
  dentist: IDentistMin;
  patient: IPatientMin;
  message?: string | null;
  rating?: IRating;
}

export interface IAppointmentCreate {
  date: string;
  status: string;
  description: string;
  dentist: { id: number };
  patient: { id: number } | null;
}
