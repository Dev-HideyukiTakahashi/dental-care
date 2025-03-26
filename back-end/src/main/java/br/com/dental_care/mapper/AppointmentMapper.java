package br.com.dental_care.mapper;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.dto.AppointmentMinDTO;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;

public class AppointmentMapper {

    public static AppointmentDTO toDTO(Appointment entity) {

        return AppointmentDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .patientMinDTO(PatientMapper.toMinDTO(entity.getPatient()))
                .dentistMinDTO(DentistMapper.toMinDTO(entity.getDentist()))
                .build();
    }

    public static AppointmentMinDTO toMinDTO(Appointment entity) {

        return AppointmentMinDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .dentist(entity.getDentist().getName())
                .build();
    }

    public static Appointment toEntity(AppointmentDTO dto, Dentist dentist, Patient patient) {

        Appointment entity = new Appointment();
        entity.setId(dto.getId());
        entity.setDate(dto.getDate());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        entity.setDentist(dentist);
        entity.setPatient(patient);
        return entity;
    }
}
