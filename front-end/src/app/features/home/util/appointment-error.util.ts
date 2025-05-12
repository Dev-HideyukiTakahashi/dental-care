export function getAppointmentErrorMessage(error: any): string {
  const msg = error?.error?.error || 'Erro desconhecido';

  switch (msg) {
    case 'An appointment already exists for this time slot.':
      return 'Já existe uma consulta agendada para este horário.';
    case 'The time falls within another appointment slot.':
      return 'O horário está dentro do intervalo de outra consulta.';
    case 'Appointment time is outside of working hours.':
      return 'O horário da consulta está fora do horário de expediente.';
    default:
      return 'Erro ao atualizar a consulta.';
  }
}
