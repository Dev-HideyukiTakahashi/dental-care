package br.com.dental_care.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.dto.AppointmentUpdateDTO;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.exception.ScheduleConflictException;
import br.com.dental_care.mapper.AppointmentMapper;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Schedule;
import br.com.dental_care.model.User;
import br.com.dental_care.model.enums.AppointmentStatus;
import br.com.dental_care.repository.AppointmentRepository;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final ScheduleRepository scheduleRepository;
    private final AuthService authService;
    private final EmailService emailService;
    private final UserService userService;

    private static final LocalTime WORKING_HOURS_START = LocalTime.of(8, 0);
    private static final LocalTime WORKING_HOURS_END = LocalTime.of(19, 0);

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO dto) {

        Dentist dentist = validateDentist(dto.getDentistMinDTO().getId());
        Patient patient = validatePatient(dto.getPatientMinDTO().getId());
        authService.validateSelfOrAdmin(patient.getId());
        validateWorkingHours(dto.getDate());

        if (checkDentistAvailability(dentist, dto.getDate())) {
            Appointment appointment = AppointmentMapper.toEntity(dto, dentist, patient);
            appointment = appointmentRepository.save(appointment);
            Schedule schedule = createSchedule(appointment);
            dentist.getSchedules().add(schedule);
            dto = AppointmentMapper.toDTO(appointment);

            logger.info("Schedule successfully added to the dentist's schedule list.");
            logger.info("Appointment created, id: {}", appointment.getId());

            sendAppointmentConfirmationEmail(patient, appointment, dto);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findAll(Pageable pageable) {

        Page<Appointment> page;
        User loggedUser = userService.getLoggedUser();

        if (loggedUser.hasRole("ROLE_ADMIN")) {
            page = appointmentRepository.findAll(pageable);
        } else if (loggedUser.hasRole("ROLE_PATIENT")) {
            page = appointmentRepository.findByPatient_Id(pageable, loggedUser.getId());
        } else if (loggedUser.hasRole("ROLE_DENTIST")) {
            page = appointmentRepository.findByDentist_Id(pageable, loggedUser.getId());
        } else {
            logger.warn("User role is empty or not authorized");
            return Page.empty();
        }

        return page.map(appointment -> AppointmentMapper.toDTO(appointment));
    }

    @Transactional
    public Page<AppointmentDTO> findByDate(String date, Pageable pageable) {

        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        Page<Appointment> page;

        User loggedUser = userService.getLoggedUser();

        if (loggedUser.hasRole("ROLE_ADMIN")) {
            page = appointmentRepository.findByDateBetween(startOfDay, endOfDay, pageable);
        } else if (loggedUser.hasRole("ROLE_DENTIST")) {
            page = appointmentRepository.findByDentistAndDateBetween(loggedUser.getId(), startOfDay, endOfDay,
                    pageable);
        } else if (loggedUser.hasRole("ROLE_PATIENT")) {
            page = appointmentRepository.findByPatientAndDateBetween(loggedUser.getId(), startOfDay, endOfDay,
                    pageable);
        } else {
            return Page.empty();
        }

        return page.map(appointment -> AppointmentMapper.toDTO(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentDTO findById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found! ID: " + id));

        authService.validateSelfOrAdmin(appointment.getPatient().getId());
        logger.info("Appointment found, id: {}", id);
        return AppointmentMapper.toDTO(appointment);
    }

    @Transactional
    public AppointmentDTO completeAppointment(Long id) {
        Appointment appointment = validateAppointment(id);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        logger.info("Appointment completed, id: {}", appointment.getId());
        return AppointmentMapper.toDTO(appointment);
    }

    @Transactional
    public AppointmentDTO updateAppointmentDateTime(Long id, AppointmentUpdateDTO dto) {
        Appointment appointment = validateAppointment(id);
        validatePatient(appointment.getPatient().getId());

        Dentist dentist = validateDentist(appointment.getDentist().getId());
        boolean futureDate = appointment.getDate().isAfter(LocalDateTime.now());
        boolean isDentistAvailable = checkDentistAvailability(dentist, dto.getDate());

        if (isDentistAvailable && futureDate) {
            updateAppointmentDate(appointment, dto.getDate());
        }
        return AppointmentMapper.toDTO(appointment);
    }

    @Transactional
    public AppointmentDTO cancelAppointment(Long id) {
        Appointment appointment = validateAppointment(id);
        appointment.setStatus(AppointmentStatus.CANCELED);
        authService.validateSelfOrAdmin(appointment.getPatient().getId());
        deleteSchedule(appointment);
        logger.info("Appointment canceled, id: {}", appointment.getId());
        return AppointmentMapper.toDTO(appointment);
    }

    private Dentist validateDentist(Long id) {
        return dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found! ID: " + id));
    }

    private Patient validatePatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found! ID: " + id));
    }

    private void validateWorkingHours(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();

        boolean isOutsideWorkingHours = time.isBefore(WORKING_HOURS_START) || time.isAfter(WORKING_HOURS_END);
        if (isOutsideWorkingHours)
            throw new ScheduleConflictException("Appointment time is outside of working hours.");
    }

    private boolean checkDentistAvailability(Dentist dentist, LocalDateTime date) {
        for (Schedule schedule : dentist.getSchedules()) {
            LocalDateTime unavailableTimeSlot = schedule.getUnavailableTimeSlot();

            if (unavailableTimeSlot == null)
                continue;

            // Check if the current unavailable time slot matches the given appointment time
            if (unavailableTimeSlot.equals(date))
                throw new ScheduleConflictException("An appointment already exists for this time slot.");

            // Check if the given appointment time falls within one hour of an existing
            // schedule
            if (isWithinOneHour(unavailableTimeSlot, date))
                throw new ScheduleConflictException("The time falls within another appointment slot.");
        }
        return true;
    }

    private boolean isWithinOneHour(LocalDateTime unavailableTimeSlot, LocalDateTime date) {
        return date.isAfter(unavailableTimeSlot.minusHours(1)) && date.isBefore(unavailableTimeSlot.plusHours(1));
    }

    private Schedule createSchedule(Appointment appointment) {
        Schedule schedule = new Schedule();
        schedule.setDentist(appointment.getDentist());
        schedule.setUnavailableTimeSlot(appointment.getDate());
        scheduleRepository.save(schedule);
        logger.info("Schedule created, id: {}", schedule.getId());
        return schedule;
    }

    private void deleteSchedule(Appointment appointment) {
        logger.info("Initiating schedule deletion process.");
        List<Schedule> dentistSchedules = appointment.getDentist().getSchedules();
        for (Schedule schedule : dentistSchedules) {
            if (schedule.getUnavailableTimeSlot().isEqual(appointment.getDate())) {
                scheduleRepository.deleteById(schedule.getId());
                logger.info("Schedule deleted, id: {}", schedule.getId());
                break;
            }
        }
    }

    private Appointment validateAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found! Id: " + id));

        if (appointment.getStatus() == AppointmentStatus.CANCELED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED)
            throw new ScheduleConflictException("The appointment has already been completed or canceled.");

        return appointment;
    }

    private void sendAppointmentConfirmationEmail(Patient patient, Appointment appointment, AppointmentDTO dto) {
        try {
            emailService.sendAppointmentConfirmationEmail(patient, appointment);
            dto.setMessage("Appointment confirmation email sent successfully.");
        } catch (MailException e) {
            logger.warn("Appointment saved, but failed to send confirmation email: {}", e.getMessage());
            dto.setMessage("Appointment saved, but confirmation email could not be sent.");
        }
    }

    private void updateAppointmentDate(Appointment appointment, LocalDateTime newDate) {
        deleteSchedule(appointment);
        appointment.setDate(newDate);
        createSchedule(appointment);
        logger.info("Appointment date/time updated, id: {}", appointment.getId());
    }

}
