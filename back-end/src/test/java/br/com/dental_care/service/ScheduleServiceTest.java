package br.com.dental_care.service;

import br.com.dental_care.dto.AbsenceDTO;
import br.com.dental_care.dto.AbsenceRequestDTO;
import br.com.dental_care.exception.InvalidDateRangeException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.exception.ScheduleConflictException;
import br.com.dental_care.factory.AppointmentFactory;
import br.com.dental_care.factory.DentistFactory;
import br.com.dental_care.factory.ScheduleFactory;
import br.com.dental_care.factory.UserFactory;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Schedule;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.DentistRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private UserService userService;

    private Long validId;
    private Long invalidId;
    private AbsenceDTO absenceDTO;
    private AbsenceRequestDTO absenceRequestDTO;
    private Dentist dentist;
    private User user;
    private Schedule schedule;
    private LocalDateTime start;
    private LocalDateTime end;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        validId = 1L;
        invalidId = 999L;
        start = LocalDateTime.parse("2027-12-20T10:00:00");
        end = LocalDateTime.parse("2027-12-30T10:00:00");
        dentist = DentistFactory.createValidDentist();
        user = UserFactory.createValidUser();
        schedule = ScheduleFactory.createValidSchedule();
        appointment = AppointmentFactory.createValidAppointment();
        absenceRequestDTO = AbsenceRequestDTO.builder()
                .absenceStart(start)
                .absenceEnd(end)
                .build();
    }

    @Test
    void createDentistAbsence_Should_CreateAbsence_When_ValidDataProvided() {

        // Arrange
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(userService.authenticated()).thenReturn(user);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        AbsenceDTO result = scheduleService.createDentistAbsence(1L, absenceRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(absenceRequestDTO.getAbsenceStart(), result.getAbsenceStart());
        assertEquals(absenceRequestDTO.getAbsenceEnd(), result.getAbsenceEnd());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void createDentistAbsence_Should_ThrowException_When_DentistIsNotOwner() {

        user.setId(invalidId);

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(userService.authenticated()).thenReturn(user);

        Exception exception = assertThrows(ScheduleConflictException.class, () -> {
            scheduleService.createDentistAbsence(validId, absenceRequestDTO);
        });

        assertNotEquals(user.getId(), dentist.getId());
        assertEquals("You are not allowed to create an absence for another dentist.", exception.getMessage());

        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void createDentistAbsence_Should_ThrowException_When_DatesAreInvalid() {

        absenceRequestDTO = AbsenceRequestDTO.builder()
                .absenceStart(end)
                .absenceEnd(start)
                .build();

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(userService.authenticated()).thenReturn(user);

        Exception exception = assertThrows(InvalidDateRangeException.class, () -> {
            scheduleService.createDentistAbsence(validId, absenceRequestDTO);
        });

        assertEquals("The end date and time cannot be before the start date and time.", exception.getMessage());
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void createDentistAbsence_Should_ThrowException_When_DentistAlreadyAbsentInPeriod() {

        dentist.getSchedules().add(schedule);

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(userService.authenticated()).thenReturn(user);

        Exception exception = assertThrows(ScheduleConflictException.class, () -> {
            scheduleService.createDentistAbsence(validId, absenceRequestDTO);
        });

        assertEquals("Dentist already absent in this period.", exception.getMessage());
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void createDentistAbsence_Should_ThrowException_When_AppointmentExistsDuringAbsence() {

        Appointment existingAppointment = new Appointment();
        existingAppointment.setDate(LocalDateTime.parse("2027-12-25T10:30"));
        dentist.getAppointments().add(existingAppointment);

        Schedule existingAppointmentSchedule = new Schedule();
        existingAppointmentSchedule.setUnavailableTimeSlot(LocalDateTime.parse("2027-12-25T10:30"));
        dentist.getSchedules().add(existingAppointmentSchedule);

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));
        when(userService.authenticated()).thenReturn(user);

        Exception exception = assertThrows(ScheduleConflictException.class, () -> {
            scheduleService.createDentistAbsence(validId, absenceRequestDTO);
        });

        assertEquals("The dentist cannot be on leave during this period, as there is already an appointment scheduled."
                , exception.getMessage());
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void createDentistAbsence_Should_ThrowException_When_DentistNotFound() {

        when(dentistRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleService.createDentistAbsence(invalidId, absenceRequestDTO);
        });

        assertEquals("Dentist not found! ID: " + invalidId, exception.getMessage());
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void removeDentistAbsence_Should_DeleteAbsence_When_ValidIdProvided() {

        schedule.setUnavailableTimeSlot(null);
        schedule.setAbsenceEnd(LocalDateTime.now().plusDays(5));

        when(scheduleRepository.findById(validId)).thenReturn(Optional.of(schedule));
        doNothing().when(scheduleRepository).deleteById(validId);

        scheduleService.removeDentistAbsence(validId);

        assertNull(schedule.getUnavailableTimeSlot());
        verify(scheduleRepository, times(1)).findById(validId);
        verify(scheduleRepository, times(1)).deleteById(validId);
    }

    @Test
    void removeDentistAbsence_Should_ThrowException_When_ScheduleNotFound() {

        when(scheduleRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleService.removeDentistAbsence(invalidId);
        });

        assertEquals("Schedule not found! ID: " + invalidId, exception.getMessage());
        verify(scheduleRepository, times(1)).findById(invalidId);
        verify(scheduleRepository, never()).deleteById(invalidId);
    }

    @Test
    void removeDentistAbsence_Should_ThrowException_When_ScheduleIsNotAbsence() {

        schedule.setUnavailableTimeSlot(LocalDateTime.parse("2027-12-20T10:00:00"));

        when(scheduleRepository.findById(validId)).thenReturn(Optional.of(schedule));

        Exception exception = assertThrows(ScheduleConflictException.class, () -> {
            scheduleService.removeDentistAbsence(validId);
        });

        assertEquals("Dentist is not absent during this period.", exception.getMessage());
        assertNotNull(schedule.getUnavailableTimeSlot());
        verify(scheduleRepository, times(1)).findById(validId);
        verify(scheduleRepository, never()).deleteById(validId);
    }

    @Test
    void removeDentistAbsence_Should_ThrowException_When_AbsenceEndIsInPast() {

        schedule.setAbsenceEnd(LocalDateTime.now().minusDays(5));
        schedule.setUnavailableTimeSlot(null);

        when(scheduleRepository.findById(validId)).thenReturn(Optional.of(schedule));

        Exception exception = assertThrows(InvalidDateRangeException.class, () -> {
            scheduleService.removeDentistAbsence(validId);
        });

        assertEquals("Cannot remove an absence period that has already ended.", exception.getMessage());
        assertTrue(schedule.getAbsenceEnd().isBefore(LocalDateTime.now()));
        verify(scheduleRepository, times(1)).findById(validId);
        verify(scheduleRepository, never()).deleteById(validId);
    }
}
