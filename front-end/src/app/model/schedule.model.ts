export interface ISchedule {
  id: number;
  unavailableTimeSlot: string;
  absenceStart: string | null;
  absenceEnd: string | null;
}
