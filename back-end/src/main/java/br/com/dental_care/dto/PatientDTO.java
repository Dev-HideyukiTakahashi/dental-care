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
public class PatientDTO {

    @Schema(description = "ID of the patient", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Patient's medical history", example = "Root canal treatment")
    private String medicalHistory;

    @Schema(description = "Name of the patient", example = "John Doe")
    @NotBlank(message = "Name is required.")
    private String name;

    @Schema(description = "Email of the patient", example = "john.doe@example.com")
    @Email(message = "Please enter a valid email address.")
    private String email;

    @Schema(description = "Password of the patient", example = "#Password123")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters.")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter.")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter.")
    @Pattern(regexp = ".*[0-9].*", message = "Password must contain at least one number.")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "Password must contain at least one special character.")
    private String password;

    @Schema(description = "Phone number of the patient", example = "(11) 99710-2376")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Invalid phone number. Expected format: (XX) 9XXXX-XXXX.")
    private String phone;

    @Schema(description = "Roles assigned to the patient")
    private final List<RoleDTO> roles = new ArrayList<>();


    //    @Schema(description = "Appointment scheduled for the patient")
    //    private final List<Appointment> appointments = new ArrayList<>();

    //    public void addRating(Appointment appointment) {
    //        appointments.add(appointment);
    //    }
    //

    public void addRole(RoleDTO role) {
        roles.add(role);
    }


}
