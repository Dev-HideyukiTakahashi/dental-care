package br.com.dental_care.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PatientMinDTO {

    private Long id;
    private String medicalHistory;
    private String name;
    private String phone;
}
