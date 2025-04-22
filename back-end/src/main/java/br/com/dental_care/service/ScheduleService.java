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
        validateAbsenceDates(dto);
        validateAbsencePeriodNotConflict(dentist, dto);
        validateNoAppointmentsDuringAbsence(dentist, dto);

        Schedule scheduleAbsence = createAbsenceSchedule(dentist, dto);
        scheduleAbsence = scheduleRepository.save(scheduleAbsence);
        dentist.getSchedules().add(scheduleAbsence);

        logger.info("Dentist absence scheduled successfully with id: {}", scheduleAbsence.getId());
        return ScheduleMapper.toAbsenceDTO(scheduleAbsence);
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

    private void validateAbsenceDates(AbsenceRequestDTO dto) {
        if (!dto.getAbsenceEnd().isAfter(dto.getAbsenceStart()))
            throw new InvalidDateRangeException("The end date and time cannot be before the start date and time.");
    }

    private void validateAbsencePeriodNotConflict(Dentist dentist, AbsenceRequestDTO dto) {
        if (isDentistAlreadyAbsent(dentist, dto.getAbsenceStart(), dto.getAbsenceEnd()))
            throw new ScheduleConflictException("Dentist already absent in this period.");
    }

    /**
     * Checks if the given dentist is already marked as absent during the specified period.
     * This method iterates through all schedules associated with the dentist. If a schedule contains
     * an absence period that overlaps with the provided start and end times, a {@link ScheduleConflictException}
     * is thrown.
     *
     * @param dentist the dentist to check for overlapping absence
     * @param start   the start date and time of the requested absence
     * @param end     the end date and time of the requested absence
     * @return false always, since the method throws an exception in case of conflict
     * @throws ScheduleConflictException if the dentist is already absent during the given time range
     */
    private boolean isDentistAlreadyAbsent(Dentist dentist, LocalDateTime start, LocalDateTime end) {
        for (Schedule schedule : dentist.getSchedules()) {
            if (schedule.getAbsenceStart() == null) continue;

            if (schedule.getAbsenceStart().isBefore(end) && schedule.getAbsenceEnd().isAfter(start))
                throw new ScheduleConflictException("Dentist already absent in this period.");
        }
        return false;
    }

    private void validateNoAppointmentsDuringAbsence(Dentist dentist, AbsenceRequestDTO dto) {
        if (hasAppointmentsDuringAbsence(dentist, dto.getAbsenceStart(), dto.getAbsenceEnd()))
            throw new ScheduleConflictException
                    ("The dentist cannot be on leave during this period, as there is already an appointment scheduled.");
    }

    /**
     * Checks if the dentist has any scheduled appointments that overlap with the requested absence period.
     * Each appointment is represented by an {@code unavailableTimeSlot} and assumed to last 1 hour.
     *
     * @param dentist      the dentist whose schedule is being checked
     * @param absenceStart the start date and time of the absence period
     * @param absenceEnd   the end date and time of the absence period
     * @return {@code true} if any appointment overlaps with the absence period, otherwise {@code false}
     */
    private boolean hasAppointmentsDuringAbsence(Dentist dentist, LocalDateTime absenceStart, LocalDateTime absenceEnd) {
        for (Schedule schedule : dentist.getSchedules()) {
            LocalDateTime appointmentStart = schedule.getUnavailableTimeSlot();

            if (appointmentStart != null) {
                LocalDateTime appointmentEnd = appointmentStart.plusHours(1);

                if (isOverlapping(appointmentStart, appointmentEnd, absenceStart, absenceEnd))
                    return true;
            }
        }
        return false;
    }

    /**
     * Determines if an appointment period and an absence period overlap.
     *
     * @param appointmentStart the start time of the appointment
     * @param appointmentEnd   the end time of the appointment
     * @param absenceStart     the start time of the absence
     * @param absenceEnd       the end time of the absence
     * @return {@code true} if the periods overlap, otherwise {@code false}
     */
    private boolean isOverlapping(LocalDateTime appointmentStart, LocalDateTime appointmentEnd,
                                  LocalDateTime absenceStart, LocalDateTime absenceEnd) {

        //Check if the appointment start time falls within the dentist's absence period
        boolean isOverlappingWithExistingAppointment =
                (appointmentStart.isAfter(absenceStart) || appointmentStart.isEqual(absenceStart)) &&
                        (appointmentStart.isBefore(absenceEnd) || appointmentStart.isEqual(absenceEnd));

        // Checks if the dentist's absence start time falls within the period of an existing appointment.
        boolean absenceStartsDuringAppointment =
                absenceStart.isAfter(appointmentStart) && absenceStart.isBefore(appointmentEnd);

        return isOverlappingWithExistingAppointment || absenceStartsDuringAppointment;
    }

    private Schedule createAbsenceSchedule(Dentist dentist, AbsenceRequestDTO dto) {
        Schedule schedule = new Schedule();
        schedule.setDentist(dentist);
        schedule.setAbsenceStart(dto.getAbsenceStart());
        schedule.setAbsenceEnd(dto.getAbsenceEnd());
        return schedule;
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

    private void validateDentistAbsence(Schedule schedule) {
        if (schedule.getUnavailableTimeSlot() != null)
            throw new ScheduleConflictException("Dentist is not absent during this period.");
    }

    private void validateAbsenceEndNotInPast(Schedule schedule) {
        if (schedule.getAbsenceEnd().isBefore(LocalDateTime.now()))
            throw new InvalidDateRangeException("Cannot remove an absence period that has already ended.");
    }
}
