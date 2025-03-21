package br.com.dental_care.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatientMinDTO {

    private Long id;
    private String medicalHistory;
    private String name;


//    @Schema(description = "Appointment scheduled for the patient")
//    private final List<Appointment> appointments = new ArrayList<>();

//    public void addRating(Appointment appointment) {
//        appointments.add(appointment);
//    }
//

}
