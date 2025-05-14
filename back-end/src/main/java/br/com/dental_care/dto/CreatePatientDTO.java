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
public class CreatePatientDTO {

    private Long id;
    private String medicalHistory;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{6,20}$", message = "Password must be 6–20 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.")
    private String password;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Invalid phone number. Expected format: (XX) 9XXXX-XXXX.")
    private String phone;

    private final List<RoleDTO> roles = new ArrayList<>();

    private final List<AppointmentMinDTO> appointments = new ArrayList<>();

    public void addAppointment(AppointmentMinDTO appointment) {
        appointments.add(appointment);
    }

    public void addRole(RoleDTO role) {
        roles.add(role);
    }

}
