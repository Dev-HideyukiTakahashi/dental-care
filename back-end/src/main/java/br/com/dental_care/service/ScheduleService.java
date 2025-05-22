package br.com.dental_care.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.dental_care.dto.AbsenceDTO;
import br.com.dental_care.dto.AbsenceRequestDTO;
import br.com.dental_care.exception.InvalidDateRangeException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.exception.ScheduleConflictException;
import br.com.dental_care.mapper.ScheduleMapper;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Schedule;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleRepository scheduleRepository;
    private final DentistRepository dentistRepository;
    private final UserService userService;

    @Transactional
    public AbsenceDTO findSelfAbsence() {
        Dentist dentist = buildLoggedDentist();
        logger.info("Dentist absence successfully retrieved.");
        return buildAbsenceDTO(dentist);
    }

    @Transactional
    public AbsenceDTO createDentistAbsence(AbsenceRequestDTO dto) {

        Dentist dentist = buildLoggedDentist();
        validateAbsenceDates(dto.getAbsenceStart(), dto.getAbsenceEnd());
        validateNoExistingAbsence(dentist);
        validateNoAppointmentsDuringAbsence(dentist, dto.getAbsenceStart(), dto.getAbsenceEnd());

        Schedule scheduleAbsence = buildAbsenceSchedule(dentist, dto.getAbsenceStart(), dto.getAbsenceEnd());
        scheduleAbsence = scheduleRepository.save(scheduleAbsence);
        dentist.getSchedules().add(scheduleAbsence);

        logger.info("Dentist absence scheduled successfully with id: {}", scheduleAbsence.getId());
        return ScheduleMapper.toAbsenceDTO(scheduleAbsence);
    }

    @Transactional
    public void removeDentistAbsence() {
        Dentist dentist = buildLoggedDentist();
        Long scheduleId = findActiveAbsenceScheduleId(dentist);

        if (scheduleId != null) {
            scheduleRepository.deleteById(scheduleId);

            logger.info("Successfully deleted schedule ID: {}", scheduleId);
        } else {
            throw new ResourceNotFoundException("No active absence period found for deletion.");
        }

    }

    private Long findActiveAbsenceScheduleId(Dentist dentist) {
        List<Schedule> schedules = dentist.getSchedules();
        LocalDateTime now = LocalDateTime.now();

        for (int i = schedules.size() - 1; i >= 0; i--) {
            Schedule schedule = schedules.get(i);
            LocalDateTime absenceEnd = schedule.getAbsenceEnd();

            if (absenceEnd != null) {
                if (absenceEnd.isBefore(now)) {
                    throw new ScheduleConflictException("Cannot remove an absence period that has already ended.");
                }

                if (absenceEnd.isAfter(now)) {
                    return schedule.getId();
                }
            }
        }
        return null;
    }

    private AbsenceDTO buildAbsenceDTO(Dentist dentist) {
        LocalDateTime now = LocalDateTime.now();

        for (Schedule schedule : dentist.getSchedules()) {
            LocalDateTime absenceEnd = schedule.getAbsenceEnd();

            if (absenceEnd != null && absenceEnd.isAfter(now)) {
                return AbsenceDTO.builder()
                        .absenceStart(schedule.getAbsenceStart())
                        .absenceEnd(schedule.getAbsenceEnd())
                        .build();
            }
        }

        return null;
    }

    private Dentist buildLoggedDentist() {
        User user = userService.authenticated();
        return dentistRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found! ID: " + user.getId()));
    }

    private void validateAbsenceDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start))
            throw new InvalidDateRangeException("The end date and time cannot be before the start date and time.");
    }

    private void validateNoExistingAbsence(Dentist dentist) {

        for (Schedule schedule : dentist.getSchedules()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime absenceEnd = schedule.getAbsenceEnd();

            if (absenceEnd != null && absenceEnd.isAfter(now)) {
                throw new ScheduleConflictException("The dentist already has an active or scheduled leave period.");
            }
        }
    }

    private void validateNoAppointmentsDuringAbsence(Dentist dentist, LocalDateTime absenceStart,
            LocalDateTime absenceEnd) {

        for (Schedule schedule : dentist.getSchedules()) {
            LocalDateTime appointmentStart = schedule.getUnavailableTimeSlot();

            if (appointmentStart == null)
                continue; // No appointment scheduled for this time slot, skip

            LocalDateTime appointmentEnd = appointmentStart.plusHours(1); // Assuming fixed 1-hour appointment duration

            // Check if the absence period overlaps with the scheduled appointment
            boolean isAbsenceDuringAppointment = absenceStart.isBefore(appointmentEnd)
                    && absenceEnd.isAfter(appointmentStart);

            if (isAbsenceDuringAppointment)
                throw new ScheduleConflictException(
                        "The dentist cannot be on leave during this period, as there is already an appointment scheduled.");
        }
    }

    private Schedule buildAbsenceSchedule(Dentist dentist, LocalDateTime start, LocalDateTime end) {
        Schedule schedule = new Schedule();
        schedule.setDentist(dentist);
        schedule.setAbsenceStart(start);
        schedule.setAbsenceEnd(end);
        return schedule;
    }
}
