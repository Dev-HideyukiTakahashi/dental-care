package br.com.dental_care.service;

import br.com.dental_care.dto.RatingDTO;
import br.com.dental_care.exception.InvalidRatingDataException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.factory.*;
import br.com.dental_care.model.*;
import br.com.dental_care.model.enums.AppointmentStatus;
import br.com.dental_care.repository.AppointmentRepository;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RatingService ratingService;

    private RatingDTO ratingDTO;
    private Rating rating;
    private Dentist dentist;
    private Patient patient;
    private Appointment appointment;
    private User user;
    private Long validId;
    private Long invalidId;

    @BeforeEach
    void setUp() {
        ratingDTO = RatingFactory.createValidRatingDTO();
        rating = RatingFactory.createValidRating();
        dentist = DentistFactory.createValidDentist();
        patient = PatientFactory.createValidPatient();
        user = UserFactory.createValidUser();
        appointment = AppointmentFactory.createValidAppointment();
        validId = 1L;
        invalidId = 999L;
    }

    @Test
    void rateDentist_Should_SaveRatingSuccessfully_When_ValidDataProvided() {

        // Arrange
        rating.getAppointment().setStatus(AppointmentStatus.COMPLETED);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(ratingRepository.findByAppointment_Id(validId)).thenReturn(Optional.of(rating));
        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));
        when(dentistRepository.existsById(validId)).thenReturn(true);
        when(dentistRepository.getReferenceById(validId)).thenReturn(dentist);
        when(patientRepository.existsById(validId)).thenReturn(true);
        when(patientRepository.getReferenceById(validId)).thenReturn(patient);
        when(userService.authenticated()).thenReturn(user);
        when(ratingRepository.save(rating)).thenReturn(rating);

        // ACT
        ratingDTO = ratingService.rateDentist(ratingDTO);

        // Assert
        assertNotNull(ratingDTO);
        assertNotNull(rating);
        assertNotNull(appointment);
        assertNotNull(dentist);
        assertNotNull(patient);
        assertEquals(AppointmentStatus.COMPLETED, rating.getAppointment().getStatus());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());

        verify(ratingRepository, times(1)).findByAppointment_Id(validId);
        verify(appointmentRepository, times(1)).findById(validId);
        verify(dentistRepository, times(1)).existsById(validId);
        verify(dentistRepository, times(1)).getReferenceById(validId);
        verify(patientRepository, times(1)).existsById(validId);
        verify(patientRepository, times(1)).getReferenceById(validId);
        verify(userService, times(1)).authenticated();
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    void rateDentist_Should_ThrowException_When_AppointmentAlreadyRated() {

        rating.setRated(true);

        when(ratingRepository.findByAppointment_Id(validId)).thenReturn(Optional.of(rating));

        Exception exception = assertThrows(InvalidRatingDataException.class, () -> {
            ratingService.rateDentist(ratingDTO);
        });

        assertEquals("This appointment has already been rated.", exception.getMessage());
        verify(ratingRepository, times(1)).findByAppointment_Id(validId);
        verify(ratingRepository, never()).save(rating);
    }

    @Test
    void rateDentist_Should_ThrowException_When_AppointmentNotFound() {

        ratingDTO = RatingDTO.builder().appointmentId(invalidId).build();

        when(ratingRepository.findByAppointment_Id(invalidId)).thenReturn(Optional.empty());
        when(appointmentRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.rateDentist(ratingDTO);
        });

        assertEquals("Appointment not found! ID: " + invalidId, exception.getMessage());
        verify(ratingRepository, times(1)).findByAppointment_Id(invalidId);
        verify(appointmentRepository, times(1)).findById(invalidId);
    }

    @Test
    void rateDentist_Should_ThrowException_When_AppointmentStatusIsNotCompleted() {

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(ratingRepository.findByAppointment_Id(validId)).thenReturn(Optional.of(rating));
        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));

        Exception exception = assertThrows(InvalidRatingDataException.class, () -> {
            ratingService.rateDentist(ratingDTO);
        });

        assertEquals("Appointment status must be 'COMPLETED' to proceed with the rating.", exception.getMessage());
        verify(ratingRepository, times(1)).findByAppointment_Id(validId);
        verify(appointmentRepository, times(1)).findById(validId);
    }

    @Test
    void rateDentist_Should_ThrowException_When_DentistNotFound() {

        when(ratingRepository.findByAppointment_Id(validId)).thenReturn(Optional.of(rating));
        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));

        Exception exception = assertThrows(InvalidRatingDataException.class, () -> {
            ratingService.rateDentist(ratingDTO);
        });

        assertEquals("Appointment status must be 'COMPLETED' to proceed with the rating.", exception.getMessage());
        verify(ratingRepository, times(1)).findByAppointment_Id(validId);
        verify(appointmentRepository, times(1)).findById(validId);
    }

    @Test
    void rateDentist_Should_ThrowException_When_DentistNotExists() {

        appointment.setStatus(AppointmentStatus.COMPLETED);
        ratingDTO = RatingFactory.createValidRatingWithInvalidDentistDTO();

        when(ratingRepository.findByAppointment_Id(validId)).thenReturn(Optional.empty());
        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));
        when(dentistRepository.existsById(invalidId)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.rateDentist(ratingDTO);
        });

        assertEquals("Dentist not found! ID: " + invalidId, exception.getMessage());
        verify(ratingRepository, times(1)).findByAppointment_Id(validId);
        verify(appointmentRepository, times(1)).findById(validId);
        verify(dentistRepository, times(1)).existsById(invalidId);
    }

    @Test
    void rateDentist_Should_ThrowException_When_DentistNotInAppointment() {

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.getDentist().setId(999L);

        when(ratingRepository.findByAppointment_Id(validId)).thenReturn(Optional.empty());
        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));
        when(dentistRepository.existsById(validId)).thenReturn(true);

        Exception exception = assertThrows(InvalidRatingDataException.class, () -> {
            ratingService.rateDentist(ratingDTO);
        });

        assertEquals("The dentist is not associated with this appointment.", exception.getMessage());
        verify(ratingRepository, times(1)).findByAppointment_Id(validId);
        verify(appointmentRepository, times(1)).findById(validId);
        verify(dentistRepository, times(1)).existsById(validId);
    }

    @Test
    void rateDentist_Should_ThrowException_When_PatientNotExists() {

        appointment.setStatus(AppointmentStatus.COMPLETED);
        ratingDTO = RatingFactory.createValidRatingWithInvalidPatientDTO();

        when(ratingRepository.findByAppointment_Id(validId)).thenReturn(Optional.empty());
        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));
        when(dentistRepository.existsById(validId)).thenReturn(true);
        when(dentistRepository.getReferenceById(validId)).thenReturn(dentist);
        when(patientRepository.existsById(invalidId)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.rateDentist(ratingDTO);
        });

        assertEquals("Patient not found! ID: " + invalidId, exception.getMessage());
        verify(ratingRepository, times(1)).findByAppointment_Id(validId);
        verify(appointmentRepository, times(1)).findById(validId);
        verify(dentistRepository, times(1)).existsById(validId);
        verify(dentistRepository, times(1)).getReferenceById(validId);
        verify(patientRepository, times(1)).existsById(invalidId);
    }

    @Test
    void rateDentist_Should_ThrowException_When_PatientIsNotTheSameAsAuthenticatedUser() {

        appointment.setStatus(AppointmentStatus.COMPLETED);
        user.setId(999L);

        when(ratingRepository.findByAppointment_Id(validId)).thenReturn(Optional.empty());
        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));
        when(dentistRepository.existsById(validId)).thenReturn(true);
        when(dentistRepository.getReferenceById(validId)).thenReturn(dentist);
        when(patientRepository.existsById(validId)).thenReturn(true);
        when(userService.authenticated()).thenReturn(user);

        Exception exception = assertThrows(InvalidRatingDataException.class, () -> {
            ratingService.rateDentist(ratingDTO);
        });

        assertEquals("You can only submit a rating for your own appointment.", exception.getMessage());
        verify(ratingRepository, times(1)).findByAppointment_Id(validId);
        verify(appointmentRepository, times(1)).findById(validId);
        verify(dentistRepository, times(1)).existsById(validId);
        verify(dentistRepository, times(1)).getReferenceById(validId);
        verify(patientRepository, times(1)).existsById(validId);
    }


    @Test
    void rateDentist_Should_ThrowException_When_AuthenticatedUserIsNotPatient() {
    }

}
