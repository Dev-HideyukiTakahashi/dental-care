package br.com.dental_care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AbsenceRequestDTO {


    @NotNull(message = "The start date and time are required.")
    @FutureOrPresent(message = "The start date and time cannot be in the past.")
    @Schema(description = "The start date and time of the dentist's absence",
            example = "2025-12-14T10:00:00")
    private LocalDateTime absenceStart;

    @NotNull(message = "The end date and time are required.")
    @FutureOrPresent(message = "The end date and time cannot be in the past.")
    @Schema(description = "The end date and time of the dentist's absence",
            example = "2025-12-14T12:00:00")
    private LocalDateTime absenceEnd;
}
