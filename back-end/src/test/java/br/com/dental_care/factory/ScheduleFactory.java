package br.com.dental_care.factory;

import br.com.dental_care.dto.ScheduleDTO;
import br.com.dental_care.model.Schedule;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ScheduleFactory {

    public static ScheduleDTO createValidScheduleDTO() {
        return ScheduleDTO.builder()
                .id(1L)
                .unavailableTimeSlot(LocalDateTime.of(2027, 12, 20, 8, 0, 0, 0))
                .absenceStart(LocalDateTime.of(2027, 12, 20, 9, 0, 0, 0))
                .absenceEnd(LocalDateTime.of(2027, 12, 20, 12, 0, 0, 0))
                .build();
    }

    public static Schedule createValidSchedule() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setUnavailableTimeSlot(LocalDateTime.of(2027, 12, 20, 8, 0, 0, 0));
        schedule.setDentist(DentistFactory.createValidDentist());
        schedule.setAbsenceStart(LocalDateTime.of(2027, 12, 20, 9, 0, 0, 0));
        schedule.setAbsenceEnd(LocalDateTime.of(2027, 12, 20, 12, 0, 0, 0));

        return schedule;
    }
}
