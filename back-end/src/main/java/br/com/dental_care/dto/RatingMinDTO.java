package br.com.dental_care.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder
public class RatingMinDTO {

    private Long id;
    private Integer score;
    private Long appointmentId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RatingMinDTO ratingDTO = (RatingMinDTO) o;
        return Objects.equals(id, ratingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
