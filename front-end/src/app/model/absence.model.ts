import { IDentistMin } from './dentist-min.model';

export interface IAbsence {
  id?: number;
  dentist?: IDentistMin;
  absenceStart: Date;
  absenceEnd: Date;
}
