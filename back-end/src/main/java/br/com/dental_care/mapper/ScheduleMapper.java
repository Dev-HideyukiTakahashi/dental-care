package br.com.dental_care.mapper;

import br.com.dental_care.dto.ScheduleDTO;
import br.com.dental_care.model.Schedule;

public class ScheduleMapper {

    public static ScheduleDTO toDTO(Schedule entity){

        return ScheduleDTO.builder()
                .id(entity.getId())
                .unavailableTimeSlot(entity.getUnavailableTimeSlot())
                .build();
    }

}
