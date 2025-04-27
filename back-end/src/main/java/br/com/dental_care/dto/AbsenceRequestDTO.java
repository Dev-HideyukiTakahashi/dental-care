package br.com.dental_care.dto;

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
    private LocalDateTime absenceStart;

    @NotNull(message = "The end date and time are required.")
    @FutureOrPresent(message = "The end date and time cannot be in the past.")
    private LocalDateTime absenceEnd;
}
