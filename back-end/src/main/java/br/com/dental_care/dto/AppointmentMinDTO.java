package br.com.dental_care.dto;

import br.com.dental_care.model.enums.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentMinDTO {

  private Long id;
  private LocalDateTime date;
  private Status status;
  private String description;
  private String dentist;
}
