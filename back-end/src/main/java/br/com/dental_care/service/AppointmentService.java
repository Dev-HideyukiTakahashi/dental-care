package br.com.dental_care.service;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.exception.ScheduleConflictException;
import br.com.dental_care.mapper.AppointmentMapper;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Schedule;
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

        if (isDentistAvailable) {
            Appointment appointment = AppointmentMapper.toEntity(dto, dentist, patient);
            appointment = appointmentRepository.save(appointment);
            createSchedule(appointment);
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

    private boolean checkDentistAvailability(Dentist dentist, LocalDateTime date) {
        for (Schedule schedule : dentist.getSchedules()) {

            boolean existingAppointment = schedule.getUnavailableTimeSlot().equals(date);
            if (existingAppointment)
                throw new ScheduleConflictException("An appointment already " +
                                                    "exists for this time slot.");

            /**
             Checks if the desired time falls within the 1-hour window
             before or after an existing appointment's time.
             If the time is within this window, a ScheduleConflictException is thrown
             indicating a scheduling conflict.me falls within the conflicting time slot
             */
            LocalDateTime minusOneHour = schedule.getUnavailableTimeSlot().minusHours(1);
            LocalDateTime plusOneHour = schedule.getUnavailableTimeSlot().plusHours(1);
            if (date.isAfter(minusOneHour) && date.isBefore(plusOneHour))
                throw new ScheduleConflictException("The time falls within another appointment slot.");
        }
        return true;
    }

    private void createSchedule(Appointment appointment) {
        Schedule schedule = new Schedule();
        schedule.setDentist(appointment.getDentist());
        schedule.setUnavailableTimeSlot(appointment.getDate());
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public AppointmentDTO findById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found! ID: " + id));
        logger.info("Appointment found, id: {}", id);
        return AppointmentMapper.toDTO(appointment);
    }


}
