package br.com.dental_care.service;

import br.com.dental_care.dto.EmailDTO;
import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.exception.ForbiddenException;
import br.com.dental_care.exception.RegistrationDataException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.mapper.PatientMapper;
import br.com.dental_care.model.PasswordRecover;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Role;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.PasswordRecoverRepository;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final PasswordRecoverRepository passwordRecoverRepository;
    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Transactional
    public PatientDTO registerPatient(PatientDTO dto) {
        validateEmail(dto.getEmail());

        Patient patient = buildPatientFromDTO(dto);
        patientRepository.save(patient);

        logger.info("Patient registered successfully: {}", dto.getEmail());
        return PatientMapper.toDTO(patient);
    }

    @Transactional
    public void createRecoverToken(EmailDTO body) {
        userRepository.findByEmail(body.getEmail()).orElseThrow(() ->
                new ResourceNotFoundException("Email not found."));

        PasswordRecover entity = buildPasswordRecover(body);
        passwordRecoverRepository.save(entity);
        emailService.sendPasswordResetTokenEmail(body.getEmail(), entity.getToken());
    }

    private PasswordRecover buildPasswordRecover(EmailDTO body) {
        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(UUID.randomUUID().toString());
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60));
        return entity;
    }

    private void validateEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            logger.warn("Attempt to register with already existing email: {}", email);
            throw new RegistrationDataException("Email already registered.");
        }
    }

    private Patient buildPatientFromDTO(PatientDTO dto) {
        Patient patient = new Patient();

        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setPassword(passwordEncoder.encode(dto.getPassword()));
        patient.addRole(new Role(2L, "ROLE_PATIENT"));
        patient.setMedicalHistory("");

        return patient;
    }

    public void validateSelfOrAdmin(Long userId) {
        User userLogged = userService.authenticated();
        if(!userLogged.hasRole("ROLE_ADMIN") && !userLogged.getId().equals(userId))
            throw new ForbiddenException("Access denied: You do not have permission to perform this action");
    }

}
