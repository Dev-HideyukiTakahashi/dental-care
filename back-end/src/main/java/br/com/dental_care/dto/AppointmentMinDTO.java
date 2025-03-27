package br.com.dental_care.dto;

import br.com.dental_care.model.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentMinDTO {

  private Long id;
  private LocalDateTime date;
  private AppointmentStatus status;
  private String description;
  private String dentist;
}
