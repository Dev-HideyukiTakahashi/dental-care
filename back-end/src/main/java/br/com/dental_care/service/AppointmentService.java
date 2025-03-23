package br.com.dental_care.service;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.dto.ScheduleDTO;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.AppointmentMapper;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.repository.AppointmentRepository;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final Logger logger = LoggerFactory.getLogger(DentistService.class);
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;


    public AppointmentDTO createAppointment(AppointmentDTO dto){
//        validateData(dto);
        Appointment appointment = AppointmentMapper.toEntity(dto);
        appointmentRepository.save(appointment);

        return null;
    }

    @Transactional(readOnly = true)
    public AppointmentDTO findById(Long id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found! ID: " + id));
        logger.info("Appointment found, id: {}", id);
        return AppointmentMapper.toDTO(appointment);
    }


//    private void validateData(AppointmentDTO dto) {
//        validateEntities(dto.getPatientId(), dto.getDentistId());
//        checkDentistAvailability(dto.getDentistId(), dto.getSchedules());
//    }

    private void validateEntities(Long patientId, Long dentistId) {
        if(!patientRepository.existsById(patientId))
            throw new ResourceNotFoundException("Patient does not exist with id: " + patientId);
        if(!dentistRepository.existsById(dentistId))
            throw new ResourceNotFoundException("Dentist does not exist with id: " + patientId);
    }

    private void checkDentistAvailability(Long dentistId, List<ScheduleDTO> schedules) {}
}
