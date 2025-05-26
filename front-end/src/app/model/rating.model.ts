export interface IRating {
  id?: number;
  score: number;
  comment: string;
  date?: string;
  patientId: number;
  dentistId: number;
  appointmentId: number;
  rated?: boolean;
}

export interface IRatingCreate {
  score: number;
  comment: string;
  patientId: number;
  dentistId: number;
  appointmentId: number;
}
