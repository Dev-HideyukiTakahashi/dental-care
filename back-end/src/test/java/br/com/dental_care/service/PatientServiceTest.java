package br.com.dental_care.service;

import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.dto.PatientMinDTO;
import br.com.dental_care.exception.DatabaseException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.factory.PatientFactory;
import br.com.dental_care.factory.UserFactory;
import br.com.dental_care.model.Patient;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.RoleRepository;
import br.com.dental_care.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    private PatientDTO patientDTO;
    private Patient patient;
    private Long validId;
    private Long dependentId;
    private Long invalidId;
    private String validEmail;
    private String existingEmail;
    private User user;

    @BeforeEach
    void setUp() {
        patientDTO = PatientFactory.createValidPatientDTO();
        patient = PatientFactory.createValidPatient();
        validId = 1L;
        dependentId = 2L;
        invalidId = 999L;
        validEmail = "john.doe@example.com";
        existingEmail = "john.doe@example.com";
        user = UserFactory.createValidUser();
    }

    @Test
    void findById_Should_returnPatientDTO_When_IdExists() {

        // Arrange
        when(patientRepository.findById(validId)).thenReturn(Optional.of(patient));

        // Act
        patientDTO = patientService.findById(validId);

        // Assert
        assertNotNull(patientDTO);
        assertEquals(patient.getId(), patientDTO.getId());
        assertEquals(patient.getName(), patientDTO.getName());
        assertEquals(patient.getEmail(), patientDTO.getEmail());
        assertEquals(patient.getAppointments().size(), patientDTO.getAppointments().size());
        assertEquals(patient.getMedicalHistory(), patientDTO.getMedicalHistory());
        verify(patientRepository, times(1)).findById(validId);
    }

    @Test
    void findById_Should_throwResourceNotFoundException_When_IdDoesNotExist() {

        when(patientRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            patientService.findById(invalidId);
        });

        assertEquals("Patient not found! ID: " + invalidId, exception.getMessage());
        verify(patientRepository, times(1)).findById(invalidId);
    }

    @Test
    void findAll_Should_returnPageOfPatientMinDto_When_patientsExist() {

        List<Patient> patients = List.of(patient);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = new PageImpl<>(patients, pageable, patients.size());

        when(patientRepository.searchAll(pageable)).thenReturn(page);

        Page<PatientMinDTO> dto = patientService.findAll(pageable);

        assertNotNull(dto);
        assertTrue(dto.getSize() > 0);
        assertEquals(patient.getId(), dto.getContent().get(0).getId());
        assertEquals(patient.getName(), dto.getContent().get(0).getName());
    }

    @Test
    void save_Should_returnPatientDto_When_dataIsValid() {

        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(passwordEncoder.encode(patientDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.existsById(2L)).thenReturn(true);

        patientDTO = patientService.save(patientDTO);

        assertNotNull(patientDTO);
        assertEquals(1L, patientDTO.getId());
        assertEquals("John Doe", patientDTO.getName());
        assertEquals("(11) 99710-2376", patientDTO.getPhone());
        assertEquals("No known allergies", patientDTO.getMedicalHistory());
        assertEquals(validEmail, patientDTO.getEmail());
        assertEquals(1, patientDTO.getRoles().size());
        assertEquals(2L, patientDTO.getRoles().get(0).getId());

        verify(userRepository, times(1)).findByEmail(validEmail);
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(roleRepository, times(1)).existsById(2L);
    }

    @Test
    void save_Should_throwDatabaseException_When_emailAlreadyExists() {

        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(DatabaseException.class, () -> {
            patientService.save(patientDTO);
        });

        assertEquals("Email already exists.", exception.getMessage());
        verify(patientRepository, never()).save(any());
        verify(roleRepository, never()).existsById(anyLong());
        verify(userRepository, times(1)).findByEmail(validEmail);
    }

    @Test
    void update_Should_returnUpdatedPatientDto_When_dataIsValid() {

        when(patientRepository.getReferenceById(validId)).thenReturn(patient);
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(passwordEncoder.encode(patientDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.existsById(2L)).thenReturn(true);
        doNothing().when(authService).validateSelfOrAdmin(validId);

        PatientDTO updatedDTO = patientService.update(patientDTO, validId);

        assertNotNull(updatedDTO);
        assertNotNull(updatedDTO.getId());
        assertEquals(patientDTO.getName(), updatedDTO.getName());
        assertEquals(patientDTO.getEmail(), updatedDTO.getEmail());
        assertEquals(patientDTO.getPhone(), updatedDTO.getPhone());
        assertEquals(patientDTO.getMedicalHistory(), updatedDTO.getMedicalHistory());

        verify(patientRepository).getReferenceById(validId);
        verify(userRepository).findByEmail(validEmail);
        verify(roleRepository).existsById(2L);
        verify(patientRepository).save(any(Patient.class));
        verify(authService).validateSelfOrAdmin(validId);
    }

    @Test
    void update_Should_throwResourceNotFoundException_When_patientDoesNotExist() {

        when(patientRepository.getReferenceById(invalidId))
                .thenThrow(new ResourceNotFoundException("Patient not found! ID: " + invalidId));

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->{
            patientService.update(patientDTO, invalidId);
        });

        assertEquals("Patient not found! ID: " + invalidId, exception.getMessage());
        verify(patientRepository, times(1)).getReferenceById(invalidId);
    }

    @Test
    void update_Should_throwDatabaseException_When_emailAlreadyUsed() {

        user.setId(999L);

        when(patientRepository.getReferenceById(validId)).thenReturn(patient);
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(DatabaseException.class, () ->{
            patientService.update(patientDTO, validId);
        });

        assertEquals("Email already exists.", exception.getMessage());
        verify(patientRepository, times(1)).getReferenceById(validId);
        verify(userRepository, times(1)).findByEmail(existingEmail);
    }

    @Test
    void deleteById_Should_deletePatient_When_idExists() {

        when(patientRepository.existsById(validId)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(validId);

        patientService.deleteById(validId);

        verify(patientRepository, times(1)).existsById(validId);
        verify(patientRepository, times(1)).deleteById(validId);
    }

    @Test
    void deleteById_Should_throwResourceNotFoundException_When_idDoesNotExist() {

        when(patientRepository.existsById(invalidId)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deleteById(invalidId);
        });

        assertEquals("Patient not found! ID: " + invalidId, exception.getMessage());
        verify(patientRepository, times(1)).existsById(invalidId);
    }

    @Test
    void deleteById_Should_throwDatabaseException_When_integrityViolationOccurs() {

        when(patientRepository.existsById(dependentId)).thenReturn(true);
        doThrow(new DatabaseException("Database error: Data integrity rules were violated."))
                .when(patientRepository).deleteById(dependentId);

        Exception exception = assertThrows(DatabaseException.class, () -> {
            patientService.deleteById(dependentId);
        });

        assertEquals("Database error: Data integrity rules were violated.", exception.getMessage());
        verify(patientRepository, times(1)).existsById(dependentId);
        verify(patientRepository, times(1)).deleteById(dependentId);
    }

    @Test
    void copyToEntity_Should_copyAllFieldsCorrectly_When_validDtoProvided() {

        when(passwordEncoder.encode("#Password123")).thenReturn("encodedPassword");

        PatientDTO dto = PatientDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .phone("123456789")
                .medicalHistory("None")
                .password("#Password123")
                .build();

        Patient entity = new Patient();

        patientService.copyToEntity(entity, dto);

        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getPhone(), entity.getPhone());
        assertEquals(dto.getMedicalHistory(), entity.getMedicalHistory());
    }
  }
