package br.com.dental_care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class RatingDTO {

  @Schema(description = "Unique identifier of the rating", example = "1",
          accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Score given to the dentist, ranging from 0 to 10", example = "8")
  private Integer score;

  @Schema(description = "Comment left by the patient about the service",
          example = "Great service, highly recommend!")
  private String comment;

  @Schema(description = "Date and time when the rating was recorded", example = "2025-12-27T15:30:00")
  private LocalDateTime date;

  @Schema(description = "Indicates if the appointment has already been rated", example = "false")
  private boolean hasBeenRated;

  @Schema(description = "Identifier of the patient who made the rating", example = "2")
  private Long patientId;

  @Schema(description = "Identifier of the dentist being rated", example = "4")
  private Long dentistId;

  @Schema(description = "Identifier of the appointment associated with the rating", example = "7")
  private Long appointmentId;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    RatingDTO ratingDTO = (RatingDTO) o;
    return Objects.equals(id, ratingDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
