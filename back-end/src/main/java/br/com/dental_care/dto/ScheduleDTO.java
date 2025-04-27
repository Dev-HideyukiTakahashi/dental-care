package br.com.dental_care.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleDTO {


    private Long id;
    private LocalDateTime unavailableTimeSlot;
    private LocalDateTime absenceStart;
    private LocalDateTime absenceEnd;
}
