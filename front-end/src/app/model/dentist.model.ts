import { IRating } from './rating.model';
import { IRole } from './role.model';
import { ISchedule } from './schedule.model';

export interface IDentist {
  id?: number;
  speciality: string;
  registrationNumber: string;
  score?: number;
  name: string;
  email: string;
  password?: string;
  phone: string;
  roles?: IRole[];
  ratings?: IRating[];
  schedules?: ISchedule[];
}
