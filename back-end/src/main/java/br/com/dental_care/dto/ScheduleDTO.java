package br.com.dental_care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleDTO {

    @Schema(description = "ID of the schedule", example = "2", accessMode =
            Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Time slots that are unavailable for scheduling")
    private LocalDateTime unavailableTimeSlot;

}
