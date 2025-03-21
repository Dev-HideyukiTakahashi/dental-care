package br.com.dental_care.dto;

import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.enums.Status;
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
  private Dentist dentist;

  @NotBlank(message = "Patient is required.")
  private Patient patient;
  
}
