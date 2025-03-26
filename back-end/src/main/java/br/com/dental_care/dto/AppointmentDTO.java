package br.com.dental_care.dto;

import br.com.dental_care.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentDTO {

  @Schema(description = "ID of the dentist", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @NotNull(message = "The date is required.")
  @FutureOrPresent(message = "The date cannot be in the past.")
  @Schema(description = "Date of appointment", example = "2025-12-21T06:00:00")
  private LocalDateTime date;

  @Schema(description = "Status of appointment", example = "SCHEDULED")
  private Status status;

  @NotBlank(message = "Description is required.")
  @Schema(description = "Description of appointment", example = "Routine check-up with cleaning")
  private String description;

  @JsonProperty("dentist")
  private DentistMinDTO dentistMinDTO;

  @JsonProperty("patient")
  private PatientMinDTO patientMinDTO;
}
