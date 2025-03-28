package br.com.dental_care.service;

import br.com.dental_care.dto.RatingDTO;
import br.com.dental_care.exception.InvalidRatingDataException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.RatingMapper;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Rating;
import br.com.dental_care.model.enums.AppointmentStatus;
import br.com.dental_care.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final Logger logger = LoggerFactory.getLogger(RatingService.class);
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final RatingRepository ratingRepository;


    @Transactional
    public RatingDTO rateDentist(RatingDTO dto) {

        validateAlreadyRated(dto);
        Appointment appointment = validateAppointment(dto.getAppointmentId());
        Dentist dentist = validateDentist(dto.getDentistId(), appointment);
        Patient patient = validatePatient(dto.getPatientId());

        Rating rating = RatingMapper.toEntity(dto, dentist, patient, appointment);
        rating = ratingRepository.save(rating);

        updateAverageRating(dentist, rating);

        logger.info("Rating successfully created for dentist with ID: {}", dto.getDentistId());
        return RatingMapper.toDTO(rating);
    }

    private void validateAlreadyRated(RatingDTO dto) {
        Optional<Rating> rating = ratingRepository.findByAppointment_Id(dto.getAppointmentId());
        if (rating.isPresent() && rating.get().isRated())
            throw new InvalidRatingDataException("This appointment has already been rated.");
    }

    private Appointment validateAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new
                        ResourceNotFoundException("Appointment not found! ID: " + appointmentId));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED)
            throw new InvalidRatingDataException
                    ("Appointment status must be 'COMPLETED' to proceed with the rating.");

        return appointment;
    }

    private Dentist validateDentist(Long dentistId, Appointment appointment) {
        if (!dentistRepository.existsById(dentistId))
            throw new ResourceNotFoundException("Dentist not found! ID: " + dentistId);

        if (!appointment.getDentist().getId().equals(dentistId))
            throw new InvalidRatingDataException("The dentist is not associated with this appointment.");

        return dentistRepository.getReferenceById(dentistId);
    }

    private Patient validatePatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found! ID: " + patientId);
        }

        // TODO: VALIDATE PATIENT LOGGED IN CONTEXT

        return patientRepository.getReferenceById(patientId);
    }

    private void updateAverageRating(Dentist dentist, Rating newRating) {
        dentist.getRatings().add(newRating);
        double sum = 0;
        for (Rating rating : dentist.getRatings()) {
            sum += rating.getScore();
        }
        int average = (int) Math.ceil(sum / dentist.getRatings().size());
        dentist.setScore(average);
    }

}
