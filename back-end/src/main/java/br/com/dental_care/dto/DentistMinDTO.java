package br.com.dental_care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DentistMinDTO {


    @Schema(description = "ID of the dentist", example = "4")
    private Long id;

    @Schema(description = "Specialty of the dentist", example = "Orthodontics", accessMode = Schema.AccessMode.READ_ONLY)
    private String speciality;

    @Schema(description = "Name of the dentist", example = "Dr. John Doe", accessMode = Schema.AccessMode.READ_ONLY)
    private String name;

    @Schema(description = "Dentist's registration number", example = "DR12345", accessMode = Schema.AccessMode.READ_ONLY)
    private String registrationNumber;
}
