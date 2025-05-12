package br.com.dental_care.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointmentUpdateDTO {

    @NotNull
    @FutureOrPresent(message = "The date cannot be in the past.")
    private LocalDateTime date;

}
