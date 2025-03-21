package br.com.dental_care.mapper;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.model.Appointment;

public class AppointmentMapper {

    public static AppointmentDTO toDTO(Appointment entity){
        return AppointmentDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .dentist(entity.getDentist())
                .patient(entity.getPatient())
                .build();
    }

    public static Appointment toEntity(AppointmentDTO dto){
         Appointment entity = new Appointment();
         entity.setId(dto.getId());
         entity.setDate(dto.getDate());
         entity.setStatus(dto.getStatus());
         entity.setDescription(dto.getDescription());
         entity.setDentist(dto.getDentist());
         entity.setPatient(dto.getPatient());
         return entity;
    }
}
