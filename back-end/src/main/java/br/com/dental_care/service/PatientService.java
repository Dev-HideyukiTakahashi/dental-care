package br.com.dental_care.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.dental_care.dto.CreatePatientDTO;
import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.dto.PatientMinDTO;
import br.com.dental_care.dto.UpdatePatientDTO;
import br.com.dental_care.exception.DatabaseException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.PatientMapper;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Role;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private final UserRepository userRepository;

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
    public PatientDTO save(CreatePatientDTO dto) {
        Patient patient = new Patient();
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {
            throw new DatabaseException("Email already exists.");
        });

        copyToEntity(patient, dto);
        patient = patientRepository.save(patient);
        logger.info("New patient has been created with id: {}", patient.getId());
        return PatientMapper.toDTO(patient);
    }

    @Transactional
    public PatientDTO update(UpdatePatientDTO dto, Long id) {
        try {
            Patient patient = patientRepository.getReferenceById(id);
            validateEmail(dto.getEmail(), patient.getId());
            authService.validateSelfOrAdmin(patient.getId());
            copyToUpdateEntity(patient, dto);
            patient = patientRepository.save(patient);
            logger.info("Patient updated successfully, id: {}", patient.getId());
            return PatientMapper.toDTO(patient);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Patient not found! ID: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Email already exists.");
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

    public void copyToEntity(Patient patient, CreatePatientDTO dto) {
        patient.setName(dto.getName());
        patient.setPassword(passwordEncoder.encode(dto.getPassword()));
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.getRoles().clear();
        patient.getRoles().add(new Role(2L, "ROLE_PATIENT"));
    }

    public void copyToUpdateEntity(Patient patient, UpdatePatientDTO dto) {
        patient.setName(dto.getName());
        if (dto.getPassword() != null) {
            patient.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.getRoles().clear();
        patient.getRoles().add(new Role(2L, "ROLE_PATIENT"));
    }

    private void validateEmail(String email, Long patientId) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(patientId))
            throw new DatabaseException("Email already exists.");
    }
}
