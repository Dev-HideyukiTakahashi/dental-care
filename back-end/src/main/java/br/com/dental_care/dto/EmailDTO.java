package br.com.dental_care.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailDTO {

    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    private String email;
}
