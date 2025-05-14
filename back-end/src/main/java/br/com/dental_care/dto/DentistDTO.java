package br.com.dental_care.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DentistDTO {

    private Long id;
    private String speciality;
    private String registrationNumber;
    private Integer score;
    private String name;
    private String email;
    private String phone;

    private final List<RoleDTO> roles = new ArrayList<>();

    private final List<RatingDTO> ratings = new ArrayList<>();

    private final List<ScheduleDTO> schedules = new ArrayList<>();

    public void addRole(RoleDTO role) {
        roles.add(role);
    }

    public void addSchedule(ScheduleDTO schedule) {
        schedules.add(schedule);
    }

    public void addRating(RatingDTO rating) {
        ratings.add(rating);
    }
}
