package br.com.dental_care.factory;

import br.com.dental_care.dto.AppointmentMinDTO;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.enums.AppointmentStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class AppointmentFactory {

    public AppointmentMinDTO createValidAppointmentMinDTO() {
        return AppointmentMinDTO.builder()
                .id(100L)
                .date(LocalDateTime.of(2025, 4, 25, 10, 0))
                .status(AppointmentStatus.SCHEDULED)
                .description("Routine check-up")
                .dentist("Dr. Marcos Silva")
                .build();
    }

    public Appointment createValidAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setDescription("Routine check-up");

        appointment.setPatient(PatientFactory.createValidPatient());
        appointment.setDentist(DentistFactory.createValidDentist());

        return appointment;
    }
}
