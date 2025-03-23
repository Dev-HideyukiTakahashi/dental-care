package br.com.dental_care.dto;

import br.com.dental_care.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentDTO {

  private Long id;

  @NotNull(message = "The date is required.")
  @FutureOrPresent(message = "The date cannot be in the past.")
  private LocalDateTime date;

  private Status status;

  @NotBlank(message = "Description is required.")
  private String description;

  @NotBlank(message = "Dentist is required.")
  @JsonProperty("dentist")
  private DentistMinDTO dentistMinDTO;

  @NotBlank(message = "Patient is required.")
  @JsonProperty("patient")
  private PatientMinDTO patientMinDTO;
}
