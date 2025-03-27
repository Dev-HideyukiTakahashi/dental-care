package br.com.dental_care.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AbsenceDTO {

    private Long id;
    private DentistMinDTO dentist;
    private LocalDateTime absenceStart;
    private LocalDateTime absenceEnd;
}
