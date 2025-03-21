package br.com.dental_care.service;

import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.dto.PatientMinDTO;
import br.com.dental_care.dto.RoleDTO;
import br.com.dental_care.exception.DatabaseException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.PatientMapper;
import br.com.dental_care.mapper.RoleMapper;
import br.com.dental_care.model.Patient;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(PatientService.class);


    @Transactional(readOnly = true)
    public PatientDTO findById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found! ID: " + id));

        logger.info("Patient found, id: {}", id);
        return PatientMapper.toDTO(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientMinDTO> findAll(Pageable pageable) {
        Page<Patient> page = patientRepository.searchAll(pageable);
        return page.map(p -> PatientMapper.toMinDTO(p));
    }

    @Transactional
    public PatientDTO save(PatientDTO dto) {
        Patient patient = new Patient();
        copyToEntity(patient, dto);
        patient = patientRepository.save(patient);
        logger.info("New patient has been created with id: {}", patient.getId());
        return PatientMapper.toDTO(patient);
    }

    @Transactional
    public PatientDTO update(PatientDTO dto, Long id) {
        try {
            Patient patient = patientRepository.getReferenceById(id);
            copyToEntity(patient, dto);
            patient = patientRepository.save(patient);
            logger.info("Patient updated successfully, id: {}", patient.getId());
            return PatientMapper.toDTO(patient);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Patient not found! ID: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(Long id) {
        if (!patientRepository.existsById(id))
            throw new ResourceNotFoundException("Patient not found! ID: " + id);
        try {
            patientRepository.deleteById(id);
            logger.info("Patient deleted, id: {}", id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Database error: Data integrity rules were violated.");
        }
    }

    public void copyToEntity(Patient patient, PatientDTO dto) {
        patient.setName(dto.getName());
        patient.setPassword(dto.getPassword());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.getRoles().clear();
        checkRoles(patient, dto);
    }

    private void checkRoles(Patient patient, PatientDTO dto) {
        for (RoleDTO role : dto.getRoles()) {
            if (!roleRepository.existsById(role.getId())){
                logger.warn("Attempted to create a patient with a non-existent role");
                throw new ResourceNotFoundException("Role does not exist, id: " + role.getId());
            }
            patient.getRoles().add(RoleMapper.toEntity(role));
        }
    }
}
