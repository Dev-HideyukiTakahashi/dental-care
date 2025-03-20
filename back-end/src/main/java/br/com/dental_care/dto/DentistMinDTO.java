package br.com.dental_care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class DentistMinDTO {

    private Long id;
    private String speciality;
    private String name;

//    @Schema(description = "Ratings assigned to the dentist")
//    private final List<RatingDTO> ratings = new ArrayList<>();
//
//    @Schema(description = "Schedules assigned to the dentist")
//    private final List<ScheduleDTO> schedules = new ArrayList<>();

//    public void addRating(RatingDTO rating) {
//        ratings.add(rating);
//    }
//
//    public void addSchedule(ScheduleDTO schedule) {
//        schedules.add(schedule);
//    }
}
