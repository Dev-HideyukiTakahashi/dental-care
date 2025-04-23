package br.com.dental_care.service;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.factory.AppointmentFactory;
import br.com.dental_care.factory.DentistFactory;
import br.com.dental_care.factory.PatientFactory;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Schedule;
import br.com.dental_care.model.enums.AppointmentStatus;
import br.com.dental_care.repository.AppointmentRepository;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private AuthService authService;

    @Mock
    private EmailService emailService;

    private AppointmentDTO appointmentDTO;
    private Dentist dentist;
    private Patient patient;
    private Appointment appointment;
    private Long validId;
    private Long invalidId;

    @BeforeEach
    void setUp() {
        appointmentDTO = AppointmentFactory.createValidAppointmentDTO();
        dentist = DentistFactory.createValidDentist();
        patient = PatientFactory.createValidPatient();
        appointment = AppointmentFactory.createValidAppointment();
        validId = 1L;
        invalidId = 999L;
    }

    @Test
    void createAppointment_Should_returnAppointmentDTO_When_CreateAppointmentIsSuccessful() {

        when(dentistRepository.existsById(validId)).thenReturn(true);
        when(dentistRepository.getReferenceById(validId)).thenReturn(dentist);
        when(patientRepository.existsById(validId)).thenReturn(true);
        when(patientRepository.getReferenceById(validId)).thenReturn(patient);
        doNothing().when(authService).validateSelfOrAdmin(validId);
        when(appointmentRepository.save(any())).thenReturn(appointment);
        when(scheduleRepository.save(any())).thenReturn(new Schedule());
        doNothing().when(emailService).sendAppointmentConfirmationEmail(any(), any());

        AppointmentDTO dto = appointmentService.createAppointment(appointmentDTO);

        assertNotNull(dto);
        assertEquals(appointmentDTO.getDate(), dto.getDate());
        assertEquals(appointmentDTO.getDescription(), dto.getDescription());
        assertEquals(appointmentDTO.getDentistMinDTO().getName(), dto.getDentistMinDTO().getName());
        assertEquals(appointmentDTO.getPatientMinDTO().getName(), dto.getPatientMinDTO().getName());
        assertEquals("Appointment confirmation email sent successfully.", dto.getMessage());
        verify(appointmentRepository).save(any());
        verify(scheduleRepository).save(any());
        verify(emailService).sendAppointmentConfirmationEmail(any(), any());
    }

    @Test
    void createAppointment_Should_throwResourceNotFoundException_When_DentistNotFound() {

        appointmentDTO = AppointmentFactory.createValidAppointmentDTOWithInvalidDentist();

        when(dentistRepository.existsById(invalidId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(appointmentDTO));

        assertEquals("Dentist not found! ID: " + invalidId, exception.getMessage());

        verify(dentistRepository, times(1)).existsById(invalidId);
    }

    @Test
    void createAppointment_Should_throwResourceNotFoundException_When_PatientNotFound() {

        appointmentDTO = AppointmentFactory.createValidAppointmentDTOWithInvalidPatient();

        when(dentistRepository.existsById(validId)).thenReturn(true);
        when(patientRepository.existsById(invalidId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(appointmentDTO));

        assertEquals("Patient not found! ID: " + invalidId, exception.getMessage());

        verify(patientRepository, times(1)).existsById(invalidId);
    }

    @Test
    void cancelAppointment_Should_returnAppointmentDTOWithCanceledStatus_When_SuccessfulCanceled() {

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Schedule schedule = new Schedule();
        schedule.setId(validId);
        schedule.setUnavailableTimeSlot(appointment.getDate());
        appointment.getDentist().getSchedules().add(schedule);

        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));
        doNothing().when(scheduleRepository).deleteById(validId);

        AppointmentDTO dto = appointmentService.cancelAppointment(validId);

        assertNotNull(dto);
        assertEquals(AppointmentStatus.CANCELED, dto.getStatus());
        assertEquals(AppointmentStatus.CANCELED, appointment.getStatus());
        verify(appointmentRepository,times(1)).findById(validId);
        verify(scheduleRepository).deleteById(validId);
    }

    @Test
    void completeAppointment_Should_returnAppointmentDTOWithCompletedStatus_When_successfulCompleted() {

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));

        AppointmentDTO dto = appointmentService.completeAppointment(validId);

        assertNotNull(dto);
        assertEquals(AppointmentStatus.COMPLETED, dto.getStatus());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        verify(appointmentRepository,times(1)).findById(validId);
    }

    @Test
    void findById_Should_returnAppointmentDto_When_appointmentExists() {

        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));
        doNothing().when(authService).validateSelfOrAdmin(any());

        AppointmentDTO dto = appointmentService.findById(validId);

        assertNotNull(dto);
        assertEquals(validId, dto.getId());
        assertEquals(appointment.getStatus(), dto.getStatus());
        assertEquals(appointment.getPatient().getId(), dto.getPatientMinDTO().getId());
        verify(authService).validateSelfOrAdmin(any());
        verify(appointmentRepository, times(1)).findById(validId);
    }

    @Test
    void updateAppointmentDateTime_Should_updateDate_When_ValidAppointmentData() {

        AppointmentDTO updatedDTO = AppointmentFactory.createUpdatedAppointmentDTO();
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setDate(LocalDateTime.parse("2027-05-10T10:00:00"));

        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));
        when(patientRepository.existsById(any())).thenReturn(true);
        when(patientRepository.getReferenceById(any())).thenReturn(patient);
        when(dentistRepository.existsById(any())).thenReturn(true);
        when(dentistRepository.getReferenceById(any())).thenReturn(dentist);

        AppointmentDTO dto = appointmentService.updateAppointmentDateTime(validId, updatedDTO);

        assertEquals(updatedDTO.getDate(), dto.getDate());
        verify(appointmentRepository, times(1)).findById(validId);
        verify(patientRepository, times(1)).getReferenceById(any());
        verify(dentistRepository, times(1)).getReferenceById(any());
    }
}
