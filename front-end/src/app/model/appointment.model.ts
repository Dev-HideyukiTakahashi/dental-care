import { AppointmentStatus } from './enum/appointment-status.enum';
import { IRating } from './rating.model';

interface DentistMin {
  id: number;
  speciality: string;
  name: string;
  registrationNumber: string;
  score: number;
}

interface PatientMin {
  id: number;
  medicalHistory: string;
  name: string;
  phone: string;
}

export interface IAppointment {
  id?: number;
  date: string;
  status: AppointmentStatus;
  description: string;
  dentist: DentistMin;
  patient: PatientMin;
  message?: string | null;
  rating?: IRating;
}
