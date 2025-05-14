package br.com.dental_care.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateDentistDTO {

    private Long id;

    @NotBlank(message = "Speciality is required.")
    private String speciality;

    @NotBlank(message = "Registration number is required.")
    private String registrationNumber;
    private Integer score;

    @NotBlank(message = "Name is required.")
    private String name;

    @Email(message = "Please enter a valid email address.")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{6,20}$", message = "Password must be 6–20 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.")
    private String password;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Invalid phone number. Expected format: (XX) 9XXXX-XXXX.")
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
