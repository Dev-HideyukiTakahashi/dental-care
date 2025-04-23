package br.com.dental_care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PatientMinDTO {

    @Schema(description = "ID of the patient", example = "2")
    private Long id;

    @Schema(description = "Medical history of the patient",
            example = "No known allergies, previous surgeries: appendectomy", accessMode = Schema.AccessMode.READ_ONLY)
    private String medicalHistory;

    @Schema(description = "Name of the patient", example = "Jane Smith", accessMode = Schema.AccessMode.READ_ONLY)
    private String name;

    @Schema(description = "Phone number of the patient", example = "(11) 95555-6789",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String phone;
}
