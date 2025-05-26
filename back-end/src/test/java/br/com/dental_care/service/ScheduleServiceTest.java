package br.com.dental_care.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.dental_care.dto.AbsenceDTO;
import br.com.dental_care.dto.AbsenceRequestDTO;
import br.com.dental_care.exception.InvalidDateRangeException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.exception.ScheduleConflictException;
import br.com.dental_care.factory.DentistFactory;
import br.com.dental_care.factory.ScheduleFactory;
import br.com.dental_care.factory.UserFactory;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Schedule;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.ScheduleRepository;

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
    private AbsenceRequestDTO absenceRequestDTO;
    private Dentist dentist;
    private User user;
    private Schedule schedule;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        validId = 1L;
        invalidId = 999L;
        start = LocalDateTime.parse("2027-12-20T10:00:00");
        end = LocalDateTime.parse("2027-12-30T10:00:00");
        dentist = DentistFactory.createValidDentist();
        user = UserFactory.createValidUser();
        schedule = ScheduleFactory.createValidSchedule();
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
        AbsenceDTO result = scheduleService.createDentistAbsence(absenceRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(absenceRequestDTO.getAbsenceStart(), result.getAbsenceStart());
        assertEquals(absenceRequestDTO.getAbsenceEnd(), result.getAbsenceEnd());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
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
            scheduleService.createDentistAbsence(absenceRequestDTO);
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
            scheduleService.createDentistAbsence(absenceRequestDTO);
        });

        assertEquals("The dentist already has an active or scheduled leave period.", exception.getMessage());
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
            scheduleService.createDentistAbsence(absenceRequestDTO);
        });

        assertEquals("The dentist cannot be on leave during this period, as there is already an appointment scheduled.",
                exception.getMessage());
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void removeDentistAbsence_Should_DeleteAbsence_When_ValidIdProvided() {

        schedule.setUnavailableTimeSlot(null);
        schedule.setAbsenceEnd(LocalDateTime.now().plusDays(5));
        dentist.getSchedules().addAll(List.of(schedule));

        when(userService.authenticated()).thenReturn(user);
        when(dentistRepository.findById(user.getId())).thenReturn(Optional.of(dentist));
        doNothing().when(scheduleRepository).deleteById(validId);

        scheduleService.removeDentistAbsence();

        assertNull(schedule.getUnavailableTimeSlot());
        verify(dentistRepository, times(1)).findById(user.getId());
        verify(scheduleRepository, times(1)).deleteById(validId);
    }

    @Test
    void removeDentistAbsence_Should_ThrowException_When_ScheduleNotFound() {

        when(userService.authenticated()).thenReturn(user);
        when(dentistRepository.findById(user.getId())).thenReturn(Optional.of(dentist));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleService.removeDentistAbsence();
        });

        assertEquals("No active absence period found for deletion.", exception.getMessage());
        verify(dentistRepository, times(1)).findById(user.getId());
    }

    @Test
    void removeDentistAbsence_Should_ThrowException_When_AbsenceEndIsInPast() {

        schedule.setAbsenceEnd(LocalDateTime.now().minusDays(5));
        dentist.getSchedules().add(schedule);

        when(userService.authenticated()).thenReturn(user);
        when(dentistRepository.findById(user.getId())).thenReturn(Optional.of(dentist));

        Exception exception = assertThrows(ScheduleConflictException.class, () -> {
            scheduleService.removeDentistAbsence();
        });

        assertEquals("Cannot remove an absence period that has already ended.", exception.getMessage());
        assertTrue(schedule.getAbsenceEnd().isBefore(LocalDateTime.now()));
        verify(dentistRepository, times(1)).findById(validId);
        verify(scheduleRepository, never()).deleteById(validId);
    }

    @Test
    void findSelfAbsence_Should_ReturnAbsenceDTO_When_DentistHasActiveAbsence() {

        Schedule activeAbsence = ScheduleFactory.createValidSchedule();

        activeAbsence.setUnavailableTimeSlot(null);
        activeAbsence.setAbsenceStart(LocalDateTime.now().minusDays(1));
        activeAbsence.setAbsenceEnd(LocalDateTime.now().plusDays(5));
        dentist.getSchedules().add(activeAbsence);

        when(userService.authenticated()).thenReturn(user);
        when(dentistRepository.findById(user.getId())).thenReturn(Optional.of(dentist));

        AbsenceDTO result = scheduleService.findSelfAbsence();

        assertNotNull(result);
        assertEquals(activeAbsence.getAbsenceStart(), result.getAbsenceStart());
        assertEquals(activeAbsence.getAbsenceEnd(), result.getAbsenceEnd());

        verify(dentistRepository, times(1)).findById(validId);

    }

    @Test
    void findSelfAbsence_Should_ReturnNull_When_DentistHasNoAbsence() {

        dentist.getSchedules().clear();

        when(userService.authenticated()).thenReturn(user);
        when(dentistRepository.findById(user.getId())).thenReturn(Optional.of(dentist));

        AbsenceDTO result = scheduleService.findSelfAbsence();

        assertNull(result);
    }

    @Test
    void findSelfAbsence_Should_ThrowResourceNotFoundException_When_DentistNotFound() {

        user.setId(invalidId);

        when(userService.authenticated()).thenReturn(user);
        when(dentistRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            scheduleService.findSelfAbsence();
        });
        verify(dentistRepository, times(1)).findById(invalidId);
    }
}
