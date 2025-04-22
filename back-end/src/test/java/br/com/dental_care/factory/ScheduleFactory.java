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
                .unavailableTimeSlot(LocalDateTime.parse("2027-12-20T08:00:00"))
                .absenceStart(LocalDateTime.parse("2027-12-20T10:00:00"))
                .absenceEnd(LocalDateTime.parse("2027-12-30T10:00:00"))
                .build();
    }

    public static Schedule createValidSchedule() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setUnavailableTimeSlot(LocalDateTime.parse("2027-12-20T08:00:00"));
        schedule.setDentist(DentistFactory.createValidDentist());
        schedule.setAbsenceStart(LocalDateTime.parse("2027-12-20T10:00:00"));
        schedule.setAbsenceEnd(LocalDateTime.parse("2027-12-30T10:00:00"));

        return schedule;
    }
}
