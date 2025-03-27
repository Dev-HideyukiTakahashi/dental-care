package br.com.dental_care.mapper;

import br.com.dental_care.dto.AbsenceDTO;
import br.com.dental_care.dto.ScheduleDTO;
import br.com.dental_care.model.Schedule;

public class ScheduleMapper {

    public static ScheduleDTO toDTO(Schedule entity){

        return ScheduleDTO.builder()
                .id(entity.getId())
                .unavailableTimeSlot(entity.getUnavailableTimeSlot())
                .build();
    }

    public static AbsenceDTO toAbsenceDTO(Schedule entity){

        return AbsenceDTO.builder()
                .id(entity.getId())
                .dentist(DentistMapper.toMinDTO(entity.getDentist()))
                .absenceStart(entity.getAbsenceStart())
                .absenceEnd(entity.getAbsenceEnd())
                .build();
    }

}
