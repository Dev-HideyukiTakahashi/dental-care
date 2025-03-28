package br.com.dental_care.mapper;

import br.com.dental_care.dto.RatingDTO;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Rating;

public class RatingMapper {

    public static Rating toEntity(RatingDTO dto, Dentist dentist,
                                  Patient patient, Appointment appointment) {
        Rating rating = new Rating();
        rating.setId(dto.getId());
        rating.setScore(dto.getScore());
        rating.setComment(dto.getComment());
        rating.setDate(dto.getDate());
        rating.setRated(true);
        rating.setDentist(dentist);
        rating.setPatient(patient);
        rating.setAppointment(appointment);

        return rating;
    }

    public static RatingDTO toDTO(Rating entity) {
        return RatingDTO.builder()
                .id(entity.getId())
                .score(entity.getScore())
                .comment(entity.getComment())
                .date(entity.getDate())
                .isRated(entity.isRated())
                .dentistId(entity.getDentist().getId())
                .patientId(entity.getPatient().getId())
                .appointmentId(entity.getAppointment().getId())
                .build();
    }
}
