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

    @Schema(description = "The start date and time of the dentist's absence",
            example = "2025-12-14T10:00:00")
    private LocalDateTime absenceStart;

    @Schema(description = "The end date and time of the dentist's absence",
            example = "2025-12-14T12:00:00")
    private LocalDateTime absenceEnd;

}
