package br.com.dental_care.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.dental_care.model.enums.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder(toBuilder = true)
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "The date is required.")
    @FutureOrPresent(message = "The date cannot be in the past.")
    private LocalDateTime date;
    private AppointmentStatus status;

    @NotBlank(message = "Description is required.")
    private String description;

    @JsonProperty("dentist")
    private DentistMinDTO dentistMinDTO;

    @JsonProperty("patient")
    private PatientMinDTO patientMinDTO;

    @JsonProperty("rating")
    private RatingDTO ratingDTO;

    @Setter
    private String message;
}
