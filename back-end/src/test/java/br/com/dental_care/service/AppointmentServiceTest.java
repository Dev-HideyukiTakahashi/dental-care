package br.com.dental_care.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.exception.ScheduleConflictException;
import br.com.dental_care.factory.AppointmentFactory;
import br.com.dental_care.factory.DentistFactory;
import br.com.dental_care.factory.PatientFactory;
import br.com.dental_care.factory.UserFactory;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Role;
import br.com.dental_care.model.Schedule;
import br.com.dental_care.model.User;
import br.com.dental_care.model.enums.AppointmentStatus;
import br.com.dental_care.repository.AppointmentRepository;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.ScheduleRepository;

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
    private UserService userService;

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
    void createAppointment_Should_returnAppointmentDTOAndSendSuccessEmail_When_CreateAppointmentIsSuccessful() {

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(patientRepository.findById(validId)).thenReturn(Optional.of(patient));
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
    void createAppointment_Should_returnAppointmentDTOAndNotSendEmail_When_CreateAppointmentIsSuccessfulAndEmailFail() {

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(patientRepository.findById(validId)).thenReturn(Optional.of(patient));
        doNothing().when(authService).validateSelfOrAdmin(validId);
        when(appointmentRepository.save(any())).thenReturn(appointment);
        when(scheduleRepository.save(any())).thenReturn(new Schedule());
        doThrow(new MailException("Appointment saved, but confirmation email could not be sent.") {
        })
                .when(emailService).sendAppointmentConfirmationEmail(any(), any());

        AppointmentDTO dto = appointmentService.createAppointment(appointmentDTO);

        assertNotNull(dto);
        assertEquals(appointmentDTO.getDate(), dto.getDate());
        assertEquals(appointmentDTO.getDescription(), dto.getDescription());
        assertEquals(appointmentDTO.getDentistMinDTO().getName(), dto.getDentistMinDTO().getName());
        assertEquals(appointmentDTO.getPatientMinDTO().getName(), dto.getPatientMinDTO().getName());
        assertEquals("Appointment saved, but confirmation email could not be sent.", dto.getMessage());
        verify(appointmentRepository).save(any());
        verify(scheduleRepository).save(any());
        verify(emailService).sendAppointmentConfirmationEmail(any(), any());
    }

    @Test
    void createAppointment_Should_throwResourceNotFoundException_When_DentistNotFound() {

        appointmentDTO = AppointmentFactory.createValidAppointmentDTOWithInvalidDentist();

        when(dentistRepository.findById(invalidId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(appointmentDTO));

        assertEquals("Dentist not found! ID: " + invalidId, exception.getMessage());

        verify(dentistRepository, times(1)).findById(invalidId);
    }

    @Test
    void createAppointment_Should_throwResourceNotFoundException_When_PatientNotFound() {

        appointmentDTO = AppointmentFactory.createValidAppointmentDTOWithInvalidPatient();

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(patientRepository.findById(invalidId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(appointmentDTO));

        assertEquals("Patient not found! ID: " + invalidId, exception.getMessage());

        verify(patientRepository, times(1)).findById(invalidId);
    }

    @Test
    void createAppointment_Should_throwException_When_appointmentIsOutsideWorkingHours() {

        appointmentDTO = AppointmentFactory.createValidAppointmentDtoOutsideWorkingHours();

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(patientRepository.findById(validId)).thenReturn(Optional.of(patient));
        doNothing().when(authService).validateSelfOrAdmin(validId);

        ScheduleConflictException exception = assertThrows(
                ScheduleConflictException.class,
                () -> appointmentService.createAppointment(appointmentDTO));

        assertEquals("Appointment time is outside of working hours.", exception.getMessage());
    }

    @Test
    void createAppointment_Should_throwException_When_dentistHasExactScheduleConflict() {

        LocalDateTime alreadyScheduleTime = LocalDateTime.parse("2027-04-25T10:00");
        appointmentDTO = AppointmentFactory.createValidAppointmentDtoWithAlreadyScheduled();

        Schedule schedule = new Schedule();
        schedule.setUnavailableTimeSlot(alreadyScheduleTime);
        dentist.getSchedules().add(schedule);

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(patientRepository.findById(validId)).thenReturn(Optional.of(patient));
        doNothing().when(authService).validateSelfOrAdmin(validId);

        ScheduleConflictException exception = assertThrows(
                ScheduleConflictException.class,
                () -> appointmentService.createAppointment(appointmentDTO));

        assertEquals("An appointment already exists for this time slot.", exception.getMessage());
    }

    @Test
    void createAppointment_should_throwException_When_dentistHasNearbyScheduleConflict() {

        LocalDateTime nearbyScheduleTime = LocalDateTime.parse("2027-04-25T10:30");
        appointmentDTO = AppointmentFactory.createValidAppointmentDtoWithAlreadyScheduled(); // ("2027-04-25T10:00");

        Schedule schedule = new Schedule();
        schedule.setUnavailableTimeSlot(nearbyScheduleTime);
        dentist.getSchedules().add(schedule);

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(patientRepository.findById(validId)).thenReturn(Optional.of(patient));
        doNothing().when(authService).validateSelfOrAdmin(validId);

        ScheduleConflictException exception = assertThrows(
                ScheduleConflictException.class,
                () -> appointmentService.createAppointment(appointmentDTO));

        assertEquals("The time falls within another appointment slot.", exception.getMessage());
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
        verify(appointmentRepository, times(1)).findById(validId);
        verify(scheduleRepository).deleteById(validId);
    }

    @Test
    void cancelAppointment_Should_throwException_When_appointmentAlreadyCanceled() {

        appointment.setStatus(AppointmentStatus.CANCELED);

        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));

        ScheduleConflictException exception = assertThrows(
                ScheduleConflictException.class, () -> appointmentService.cancelAppointment(validId));

        assertEquals("The appointment has already been completed or canceled.", exception.getMessage());
    }

    @Test
    void cancelAppointment_Should_throwException_When_appointmentAlreadyCompleted() {

        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));

        ScheduleConflictException exception = assertThrows(
                ScheduleConflictException.class, () -> appointmentService.cancelAppointment(validId));

        assertEquals("The appointment has already been completed or canceled.", exception.getMessage());
    }

    @Test
    void completeAppointment_Should_returnAppointmentDTOWithCompletedStatus_When_successfulCompleted() {

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(validId)).thenReturn(Optional.of(appointment));

        AppointmentDTO dto = appointmentService.completeAppointment(validId);

        assertNotNull(dto);
        assertEquals(AppointmentStatus.COMPLETED, dto.getStatus());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        verify(appointmentRepository, times(1)).findById(validId);
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
        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(patientRepository.findById(validId)).thenReturn(Optional.of(patient));

        AppointmentDTO dto = appointmentService.updateAppointmentDateTime(validId, updatedDTO);

        assertEquals(updatedDTO.getDate(), dto.getDate());
        verify(appointmentRepository, times(1)).findById(validId);
        verify(patientRepository, times(1)).findById(any());
        verify(dentistRepository, times(1)).findById(any());
    }

    @Test
    void findAll_shouldReturnAllAppointments_whenUserIsAdmin() {
        User admin = UserFactory.createValidUser();
        admin.getRoles().clear();
        admin.addRole(new Role(2L, "ROLE_ADMIN"));

        Pageable pageable = PageRequest.of(0, 10);
        List<Appointment> appointments = List.of(AppointmentFactory.createValidAppointment());
        Page<Appointment> page = new PageImpl<>(appointments);

        when(userService.getLoggedUser()).thenReturn(admin);
        when(appointmentRepository.findAll(pageable)).thenReturn(page);

        Page<AppointmentDTO> result = appointmentService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findAll(pageable);
    }

    @Test
    void findAll_shouldReturnAppointmentsByPatient_whenUserIsPatient() {
        User patient = UserFactory.createValidUser();
        dentist.getRoles().clear();
        dentist.addRole(new Role(2L, "ROLE_PATIENT"));

        Pageable pageable = PageRequest.of(0, 10);
        List<Appointment> appointments = List.of(AppointmentFactory.createValidAppointment());
        Page<Appointment> page = new PageImpl<>(appointments);

        when(userService.getLoggedUser()).thenReturn(patient);
        when(appointmentRepository.findByPatient_Id(pageable, 1L)).thenReturn(page);

        Page<AppointmentDTO> result = appointmentService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByPatient_Id(pageable, 1L);
    }

    @Test
    void findAll_shouldReturnAppointmentsByDentist_whenUserIsDentist() {
        User dentist = UserFactory.createValidUser();
        dentist.getRoles().clear();
        dentist.addRole(new Role(3L, "ROLE_DENTIST"));

        dentist.setId(2L);
        Pageable pageable = PageRequest.of(0, 10);
        List<Appointment> appointments = List.of(AppointmentFactory.createValidAppointment());
        Page<Appointment> page = new PageImpl<>(appointments);

        when(userService.getLoggedUser()).thenReturn(dentist);
        when(appointmentRepository.findByDentist_Id(pageable, 2L)).thenReturn(page);

        Page<AppointmentDTO> result = appointmentService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByDentist_Id(pageable, 2L);
    }

    @Test
    void findAll_shouldReturnEmptyPage_whenUserRoleIsInvalid() {
        User user = UserFactory.createValidUser();
        user.getRoles().clear();

        Pageable pageable = PageRequest.of(0, 10);

        when(userService.getLoggedUser()).thenReturn(user);

        Page<AppointmentDTO> result = appointmentService.findAll(pageable);

        assertTrue(result.isEmpty());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void findByDate_shouldReturnAppointmentsBetweenDates_whenUserIsAdmin() {
        User admin = UserFactory.createValidUser();
        admin.getRoles().clear();
        admin.addRole(new Role(2L, "ROLE_ADMIN"));

        String date = "2025-05-11";
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime start = LocalDate.parse(date).atStartOfDay();
        LocalDateTime end = LocalDate.parse(date).atTime(LocalTime.MAX);
        List<Appointment> appointments = List.of(AppointmentFactory.createValidAppointment());
        Page<Appointment> page = new PageImpl<>(appointments);

        when(userService.getLoggedUser()).thenReturn(admin);
        when(appointmentRepository.findByDateBetween(start, end, pageable)).thenReturn(page);

        Page<AppointmentDTO> result = appointmentService.findByDate(date, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByDateBetween(start, end, pageable);
    }

    @Test
    void findByDate_shouldReturnAppointmentsForDentist_whenUserIsDentist() {
        User dentist = UserFactory.createValidUser();
        dentist.setId(2L);
        dentist.getRoles().clear();
        dentist.addRole(new Role(3L, "ROLE_DENTIST"));

        String date = "2025-05-11";
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime start = LocalDate.parse(date).atStartOfDay();
        LocalDateTime end = LocalDate.parse(date).atTime(LocalTime.MAX);
        List<Appointment> appointments = List.of(AppointmentFactory.createValidAppointment());
        Page<Appointment> page = new PageImpl<>(appointments);

        when(userService.getLoggedUser()).thenReturn(dentist);
        when(appointmentRepository.findByDentistAndDateBetween(2L, start, end, pageable)).thenReturn(page);

        Page<AppointmentDTO> result = appointmentService.findByDate(date, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByDentistAndDateBetween(2L, start, end, pageable);
    }

    @Test
    void findByDate_shouldReturnAppointmentsForPatient_whenUserIsPatient() {
        User patient = UserFactory.createValidUser();
        dentist.getRoles().clear();
        dentist.addRole(new Role(2L, "ROLE_PATIENT"));

        String date = "2025-05-11";
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime start = LocalDate.parse(date).atStartOfDay();
        LocalDateTime end = LocalDate.parse(date).atTime(LocalTime.MAX);
        List<Appointment> appointments = List.of(AppointmentFactory.createValidAppointment());
        Page<Appointment> page = new PageImpl<>(appointments);

        when(userService.getLoggedUser()).thenReturn(patient);
        when(appointmentRepository.findByPatientAndDateBetween(1L, start, end, pageable)).thenReturn(page);

        Page<AppointmentDTO> result = appointmentService.findByDate(date, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByPatientAndDateBetween(1L, start, end, pageable);
    }

    @Test
    void findByDate_shouldReturnEmptyPage_whenUserRoleIsInvalid() {
        User user = UserFactory.createValidUser();
        user.getRoles().clear();

        String date = "2025-05-11";
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.getLoggedUser()).thenReturn(user);

        Page<AppointmentDTO> result = appointmentService.findByDate(date, pageable);

        assertTrue(result.isEmpty());
        verifyNoInteractions(appointmentRepository);
    }

}
