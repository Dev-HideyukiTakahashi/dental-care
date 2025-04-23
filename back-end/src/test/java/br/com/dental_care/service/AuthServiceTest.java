package br.com.dental_care.service;

import br.com.dental_care.dto.EmailDTO;
import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.exception.ForbiddenException;
import br.com.dental_care.exception.RegistrationDataException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.factory.PatientFactory;
import br.com.dental_care.factory.RoleFactory;
import br.com.dental_care.factory.UserFactory;
import br.com.dental_care.model.PasswordRecover;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.Role;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.PasswordRecoverRepository;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordRecoverRepository passwordRecoverRepository;

    @Mock
    private EmailService emailService;

    private PatientDTO patientDTO;
    private EmailDTO emailDTO;
    private User user;
    private User loggedUser;
    private Patient patient;
    Field tokenMinutes;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        patientDTO = PatientFactory.createValidPatientDTO();
        user = UserFactory.createValidUser();
        loggedUser = UserFactory.createValidUser();
        patient = PatientFactory.createValidPatient();
        emailDTO = EmailDTO.builder()
                .email("maria@example.com")
                .build();

        tokenMinutes = AuthService.class.getDeclaredField("tokenMinutes");
        tokenMinutes.setAccessible(true);
        tokenMinutes.set(authService, 30L);
    }

    @Test
    void registerPatient_Should_register_When_emailIsValid() {

        when(userRepository.findByEmail(patientDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(patientDTO.getPassword())).thenReturn("encodedPwd");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = authService.registerPatient(patientDTO);

        assertEquals(patientDTO.getEmail(), result.getEmail());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void registerPatient_Should_throwException_When_emailExists() {

        when(userRepository.findByEmail(patientDTO.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RegistrationDataException.class, ()
                -> authService.registerPatient(patientDTO));

        assertEquals("Email already registered.", exception.getMessage());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void createRecoverToken_Should_sendEmail_When_emailExists() {

        when(userRepository.findByEmail(emailDTO.getEmail())).thenReturn(Optional.of(user));

        authService.createRecoverToken(emailDTO);

        verify(passwordRecoverRepository, times(1)).save(any(PasswordRecover.class));
        verify(emailService, times(1)).sendPasswordResetTokenEmail(eq(emailDTO.getEmail()), anyString());
    }

    @Test
    void createRecoverToken_Should_throwException_When_emailNotFound() {

        when(userRepository.findByEmail(emailDTO.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, ()
                -> authService.createRecoverToken(emailDTO));

        assertEquals("Email not found.", exception.getMessage());
        verify(passwordRecoverRepository, never()).save(any());
    }

    @Test
    void validateSelfOrAdmin_Should_throwException_When_userIsNotAdminOrOwner() {

        loggedUser.setId(999L);

        when(userService.authenticated()).thenReturn(loggedUser);

        Exception exception = assertThrows(ForbiddenException.class, ()
                -> authService.validateSelfOrAdmin(1L));

        assertEquals("Access denied: You do not have permission to perform this action", exception.getMessage());
    }

    @Test
    void validateSelfOrAdmin_Should_pass_When_userIsAdmin() {

        loggedUser.getRoles().add(RoleFactory.createAdminRole());

        when(userService.authenticated()).thenReturn(loggedUser);

        authService.validateSelfOrAdmin(1L);

        assertEquals(1L, loggedUser.getId());
    }

    @Test
    void validateSelfOrAdmin_Should_pass_When_userIsSelf() {

        loggedUser.setId(5L);
        loggedUser.getRoles().add(new Role(2L, "ROLE_PATIENT"));

        when(userService.authenticated()).thenReturn(loggedUser);

        authService.validateSelfOrAdmin(5L);

        assertEquals(5L, loggedUser.getId());
    }
}
