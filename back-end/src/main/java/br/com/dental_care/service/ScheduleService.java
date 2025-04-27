package br.com.dental_care.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleRepository scheduleRepository;
    private final DentistRepository dentistRepository;
    private final UserService userService;

    @Transactional
    public AbsenceDTO createDentistAbsence(Long dentistId, AbsenceRequestDTO dto) {

        Dentist dentist = getDentistById(dentistId);
        validateDentistOwnership(dentistId);
        validateAbsenceDates(dto.getAbsenceStart(), dto.getAbsenceEnd());
        validateNoExistingAbsence(dentist, dto.getAbsenceStart(), dto.getAbsenceEnd());
        validateNoAppointmentsDuringAbsence(dentist, dto.getAbsenceStart(), dto.getAbsenceEnd());

        Schedule scheduleAbsence = buildAbsenceSchedule(dentist, dto.getAbsenceStart(), dto.getAbsenceEnd());
        scheduleAbsence = scheduleRepository.save(scheduleAbsence);
        dentist.getSchedules().add(scheduleAbsence);

        logger.info("Dentist absence scheduled successfully with id: {}", scheduleAbsence.getId());
        return ScheduleMapper.toAbsenceDTO(scheduleAbsence);
    }

    @Transactional
    public void removeDentistAbsence(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found! ID: " + id));

        validateDentistAbsence(schedule);
        validateAbsenceEndNotInPast(schedule);

        scheduleRepository.deleteById(id);

        logger.info("Successfully deleted schedule ID: {}", id);
    }

    private void validateDentistOwnership(Long dentistId) {
        User user = userService.authenticated();
        if (!user.getId().equals(dentistId))
            throw new ScheduleConflictException("You are not allowed to create an absence for another dentist.");
    }

    private Dentist getDentistById(Long dentistId) {
        return dentistRepository.findById(dentistId)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found! ID: " + dentistId));
    }

    private void validateAbsenceDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start))
            throw new InvalidDateRangeException("The end date and time cannot be before the start date and time.");
    }

    private void validateNoExistingAbsence(Dentist dentist, LocalDateTime start, LocalDateTime end) {

        for (Schedule schedule : dentist.getSchedules()) {
            LocalDateTime absenceStart = schedule.getAbsenceStart();
            LocalDateTime absenceEnd = schedule.getAbsenceEnd();

            if (absenceStart == null) continue; // No absence scheduled for this schedule entry, skip

            if (absenceStart.isBefore(end) && absenceEnd.isAfter(start))
                throw new ScheduleConflictException("Dentist already absent in this period.");
        }
    }

    private void validateNoAppointmentsDuringAbsence(Dentist dentist, LocalDateTime absenceStart, LocalDateTime absenceEnd) {

        for (Schedule schedule : dentist.getSchedules()) {
            LocalDateTime appointmentStart = schedule.getUnavailableTimeSlot();

            if (appointmentStart == null) continue; // No appointment scheduled for this time slot, skip

            LocalDateTime appointmentEnd = appointmentStart.plusHours(1); // Assuming fixed 1-hour appointment duration

            // Check if the absence period overlaps with the scheduled appointment
            boolean isAbsenceDuringAppointment = absenceStart.isBefore(appointmentEnd) && absenceEnd.isAfter(appointmentStart);

            if (isAbsenceDuringAppointment)
                throw new ScheduleConflictException
                        ("The dentist cannot be on leave during this period, as there is already an appointment scheduled.");
        }
    }

    private Schedule buildAbsenceSchedule(Dentist dentist, LocalDateTime start, LocalDateTime end) {
        Schedule schedule = new Schedule();
        schedule.setDentist(dentist);
        schedule.setAbsenceStart(start);
        schedule.setAbsenceEnd(end);
        return schedule;
    }

    private void validateDentistAbsence(Schedule schedule) {
        if (schedule.getUnavailableTimeSlot() != null)
            throw new ScheduleConflictException("Dentist is not absent during this period.");
    }

    private void validateAbsenceEndNotInPast(Schedule schedule) {
        if (schedule.getAbsenceEnd().isBefore(LocalDateTime.now()))
            throw new InvalidDateRangeException("Cannot remove an absence period that has already ended.");
    }
}
