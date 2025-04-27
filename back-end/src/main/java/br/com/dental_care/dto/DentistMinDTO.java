package br.com.dental_care.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class DentistMinDTO {

    private Long id;
    private String speciality;
    private String name;
    private String registrationNumber;
    private Integer score;
}
