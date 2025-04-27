package br.com.dental_care.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder(toBuilder = true)
public class RatingDTO {

    private Long id;
    private Integer score;
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    private boolean isRated;
    private Long patientId;
    private Long dentistId;
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
