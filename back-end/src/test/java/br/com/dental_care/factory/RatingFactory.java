package br.com.dental_care.factory;

import br.com.dental_care.dto.RatingDTO;
import br.com.dental_care.dto.RatingMinDTO;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Rating;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class RatingFactory {

    public static RatingDTO createValidRatingDTO() {
        return RatingDTO.builder()
                .id(1L)
                .score(8)
                .comment("Great service, highly recommend!")
                .date(LocalDateTime.of(2025, 12, 27, 15, 30, 0, 0))
                .isRated(false)
                .patientId(1L)
                .dentistId(1L)
                .appointmentId(1L)
                .build();
    }

    public static RatingDTO createValidRatingWithInvalidDentistDTO() {
        return createValidRatingDTO()
                .toBuilder()
                .dentistId(999L)
                .build();
    }

    public static RatingDTO createValidRatingWithInvalidPatientDTO() {
        return createValidRatingDTO()
                .toBuilder()
                .patientId(999L)
                .build();
    }

    public static RatingMinDTO createValidRatingMinDTO() {
        return RatingMinDTO.builder()
                .id(1L)
                .score(8)
                .appointmentId(1L)
                .build();
    }

    public static Rating createValidRating() {
        Patient patient = PatientFactory.createValidPatient();
        Dentist dentist = DentistFactory.createValidDentist();
        Appointment appointment = AppointmentFactory.createValidAppointment();

        Rating rating = new Rating();
        rating.setId(1L);
        rating.setScore(8);
        rating.setComment("Great service, highly recommend!");
        rating.setDate(LocalDateTime.of(2025, 12, 27, 15, 30, 0, 0));
        rating.setRated(false);
        rating.setPatient(patient);
        rating.setDentist(dentist);
        rating.setAppointment(appointment);

        return rating;
    }
}
