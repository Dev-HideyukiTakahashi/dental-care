package br.com.dental_care.mapper;

import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.dto.PatientMinDTO;
import br.com.dental_care.model.Patient;

public class PatientMapper {
    public static PatientDTO toDTO(Patient entity) {

        PatientDTO dto = PatientDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password("**********")
                .phone(entity.getPhone())
                .medicalHistory(entity.getMedicalHistory())
                .build();

        entity.getAppointments().forEach(appointment ->
                dto.addAppointment(AppointmentMapper.toMinDTO(appointment)));
        entity.getRoles().forEach(role -> dto.addRole(RoleMapper.toDTO(role)));
        return dto;
    }

    public static PatientMinDTO toMinDTO(Patient entity) {

        return PatientMinDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .medicalHistory(entity.getMedicalHistory())
                .build();
    }

    public static Patient minToEntity(PatientMinDTO patientMinDTO) {

        Patient entity = new Patient();
        entity.setId(patientMinDTO.getId());
        entity.setName(patientMinDTO.getName());
        entity.setMedicalHistory(patientMinDTO.getMedicalHistory());
        return entity;
    }
}
