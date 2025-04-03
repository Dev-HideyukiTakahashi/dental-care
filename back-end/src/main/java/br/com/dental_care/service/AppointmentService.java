package br.com.dental_care.service;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.exception.ScheduleConflictException;
import br.com.dental_care.mapper.AppointmentMapper;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Schedule;
import br.com.dental_care.model.enums.AppointmentStatus;
import br.com.dental_care.repository.AppointmentRepository;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        Dentist dentist = validateDentist(dto.getDentistMinDTO().getId());
        Patient patient = validatePatient(dto.getPatientMinDTO().getId());
        boolean isDentistAvailable = checkDentistAvailability(dentist, dto.getDate());
        validateWorkingHours(dto.getDate());

        if (isDentistAvailable) {
            Appointment appointment = AppointmentMapper.toEntity(dto, dentist, patient);
            appointment = appointmentRepository.save(appointment);
            Schedule schedule = createSchedule(appointment);
            dentist.getSchedules().add(schedule);
            logger.info("Schedule successfully added to the dentist's schedule list.");
            logger.info("Appointment created, id: {}", appointment.getId());
            return AppointmentMapper.toDTO(appointment);
        }
        return null;
    }

    private Dentist validateDentist(Long id) {
        if (!dentistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dentist not found! ID: " + id);
        }
        return dentistRepository.getReferenceById(id);
    }

    private Patient validatePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found! ID: " + id);
        }
        return patientRepository.getReferenceById(id);
    }

    private void validateWorkingHours(LocalDateTime dateTime){
        LocalTime time = dateTime.toLocalTime();
        final LocalTime startTime = LocalTime.of(8, 0);
        final LocalTime endTime = LocalTime.of(19, 0);

        if(time.isBefore(startTime) || time.isAfter(endTime))
            throw new ScheduleConflictException("Appointment time is outside of working hours.");
    }

    private boolean checkDentistAvailability(Dentist dentist, LocalDateTime date) {
        for (Schedule schedule : dentist.getSchedules()) {

            boolean existingAppointment = schedule.getUnavailableTimeSlot().equals(date);
            if (existingAppointment)
                throw new ScheduleConflictException("An appointment already exists for this time slot.");

            LocalDateTime minusOneHour = schedule.getUnavailableTimeSlot().minusHours(1);
            LocalDateTime plusOneHour = schedule.getUnavailableTimeSlot().plusHours(1);
            if (date.isAfter(minusOneHour) && date.isBefore(plusOneHour))
                throw new ScheduleConflictException("The time falls within another appointment slot.");
        }
        return true;
    }

    private Schedule createSchedule(Appointment appointment) {
        Schedule schedule = new Schedule();
        schedule.setDentist(appointment.getDentist());
        schedule.setUnavailableTimeSlot(appointment.getDate());
        scheduleRepository.save(schedule);
        logger.info("Schedule created, id: {}", schedule.getId());
        return schedule;
    }

    @Transactional(readOnly = true)
    public AppointmentDTO findById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found! ID: " + id));
        logger.info("Appointment found, id: {}", id);
        return AppointmentMapper.toDTO(appointment);
    }


    @Transactional
    public AppointmentDTO cancelAppointment(Long id) {
        Appointment appointment = validateAppointment(id);
        appointment.setStatus(AppointmentStatus.CANCELED);
        deleteSchedule(appointment);
        logger.info("Appointment canceled, id: {}", appointment.getId());
        return AppointmentMapper.toDTO(appointment);
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

    @Transactional
    public AppointmentDTO updateAppointmentDateTime(Long id, AppointmentDTO dto) {
        Appointment appointment = validateAppointment(id);

        validatePatient(appointment.getPatient().getId());
        Dentist dentist = validateDentist(appointment.getDentist().getId());
        boolean isDentistAvailable = checkDentistAvailability(dentist, dto.getDate());

        if (isDentistAvailable && appointment.getDate().isAfter(LocalDateTime.now())) {
            deleteSchedule(appointment);
            appointment.setDate(dto.getDate());
            createSchedule(appointment);
            logger.info("Appointment date/time updated, id: {}", appointment.getId());
        }
        return AppointmentMapper.toDTO(appointment);
    }

}
