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

//        entity.getAppointments().forEach(appointment -> AppointmentMapper.toDTO(appointment));
        entity.getRoles().forEach(role -> dto.addRole(RoleMapper.toDTO(role)));
        return dto;
    }

    public static PatientMinDTO toMinDTO(Patient entity) {
        PatientMinDTO dto = PatientMinDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .medicalHistory(entity.getMedicalHistory())
                .build();
        return dto;
    }
}
