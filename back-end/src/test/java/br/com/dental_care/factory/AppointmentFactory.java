package br.com.dental_care.factory;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.dto.AppointmentMinDTO;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.enums.AppointmentStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class AppointmentFactory {

    public AppointmentMinDTO createValidAppointmentMinDTO() {
        return AppointmentMinDTO.builder()
                .id(1L)
                .date(LocalDateTime.parse("2027-04-25T10:00"))
                .status(AppointmentStatus.SCHEDULED)
                .description("Routine check-up")
                .dentist("Dr. Marcos Silva")
                .build();
    }

    public AppointmentDTO createValidAppointmentDTO() {
        return AppointmentDTO.builder()
                .id(1L)
                .date(LocalDateTime.parse("2027-04-25T10:00"))
                .status(AppointmentStatus.SCHEDULED)
                .description("Routine check-up")
                .dentistMinDTO(DentistFactory.createValidDentistMinDTO())
                .patientMinDTO(PatientFactory.createValidPatientMinDTO())
                .build();
    }

    public AppointmentDTO createUpdatedAppointmentDTO() {
        return AppointmentDTO.builder()
                .id(1L)
                .date(LocalDateTime.parse("2025-05-11T10:00:00"))
                .dentistMinDTO(DentistFactory.createValidDentistMinDTO())
                .patientMinDTO(PatientFactory.createValidPatientMinDTO())
                .build();
    }

    public AppointmentDTO createValidAppointmentDTOWithInvalidDentist() {
        return createValidAppointmentDTO()
                .toBuilder()
                .dentistMinDTO(DentistFactory.createInvalidDentistMinDTO())
                .build();
    }

    public AppointmentDTO createValidAppointmentDTOWithInvalidPatient() {
        return createValidAppointmentDTO()
                .toBuilder()
                .patientMinDTO(PatientFactory.createInvalidPatientMinDTO())
                .build();
    }

    public Appointment createValidAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDate(LocalDateTime.parse("2027-04-25T10:00"));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setDescription("Routine check-up");

        appointment.setPatient(PatientFactory.createValidPatient());
        appointment.setDentist(DentistFactory.createValidDentist());

        return appointment;
    }
}
