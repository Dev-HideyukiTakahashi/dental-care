package br.com.dental_care.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.dental_care.dto.DentistDTO;
import br.com.dental_care.dto.DentistMinDTO;
import br.com.dental_care.dto.UpdateDentistDTO;
import br.com.dental_care.exception.DatabaseException;
import br.com.dental_care.exception.ResourceNotFoundException;
import br.com.dental_care.factory.DentistFactory;
import br.com.dental_care.factory.UserFactory;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.User;
import br.com.dental_care.repository.DentistRepository;
import br.com.dental_care.repository.RoleRepository;
import br.com.dental_care.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class DentistServiceTest {

    @InjectMocks
    private DentistService dentistService;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    private Dentist dentist;
    private DentistDTO dentistDTO;
    private UpdateDentistDTO updateDentistDTO;
    private User user;
    private Long validId;
    private Long invalidId;
    private Long dependentId;

    @BeforeEach
    void setUp() {
        dentist = DentistFactory.createValidDentist();
        dentistDTO = DentistFactory.createValidDentistDTO();
        updateDentistDTO = DentistFactory.createValidUpdateDentistDTO();
        user = UserFactory.createValidUser();
        validId = 1L;
        dependentId = 2L;
        invalidId = 999L;
    }

    @Test
    void findById_ShouldReturnDentistDTO_WhenFound() {

        when(dentistRepository.findById(validId)).thenReturn(Optional.of(dentist));

        DentistDTO result = dentistService.findById(validId);

        assertNotNull(result);
        assertEquals("Dr. John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("Orthodontics", result.getSpeciality());
        assertEquals("DR12345", result.getRegistrationNumber());
        verify(dentistRepository, times(1)).findById(validId);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {

        when(dentistRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> dentistService.findById(invalidId));

        assertEquals("Dentist not found! ID: " + invalidId, exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnPageOfDentistMinDTO() {

        List<Dentist> dentists = List.of(dentist);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Dentist> page = new PageImpl<>(dentists, pageable, dentists.size());

        when(dentistRepository.searchAll(pageable)).thenReturn(page);

        Page<DentistMinDTO> dto = dentistService.findAll(pageable);

        assertNotNull(dto);
        assertEquals(1, dto.getContent().size());
        assertEquals("Dr. John Doe", dto.getContent().get(0).getName());
        verify(dentistRepository, times(1)).searchAll(pageable);
    }

    @Test
    void save_ShouldReturnDentistDTO_WhenSuccessful() {

        when(userRepository.findByEmail(dentistDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dentistDTO.getPassword())).thenReturn("encodedPassword");
        when(dentistRepository.save(any(Dentist.class))).thenReturn(dentist);

        DentistDTO dto = dentistService.save(dentistDTO);

        assertNotNull(dto);
        assertEquals("Dr. John Doe", dto.getName());
        verify(userRepository, times(1)).findByEmail(dentistDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(dentistDTO.getPassword());
        verify(dentistRepository, times(1)).save(any(Dentist.class));
    }

    @Test
    void save_ShouldThrowException_WhenEmailAlreadyExists() {

        when(userRepository.findByEmail(dentistDTO.getEmail())).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(dentistDTO.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(DatabaseException.class, () -> dentistService.save(dentistDTO));

        assertEquals("Email already exists.", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(dentistDTO.getEmail());
    }

    @Test
    void update_ShouldReturnUpdatedDTO_WhenSuccessful() {

        when(dentistRepository.getReferenceById(validId)).thenReturn(dentist);
        when(passwordEncoder.encode(updateDentistDTO.getPassword())).thenReturn("encodedPassword");
        when(dentistRepository.save(any(Dentist.class))).thenReturn(dentist);

        DentistDTO result = dentistService.update(updateDentistDTO, validId);

        assertNotNull(result);
        verify(dentistRepository, times(1)).getReferenceById(validId);
        verify(userRepository, times(1)).findByEmail(updateDentistDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(updateDentistDTO.getPassword());
        verify(dentistRepository, times(1)).save(any(Dentist.class));
        verify(dentistRepository, times(1)).save(any(Dentist.class));
    }

    @Test
    void update_ShouldThrowResourceNotFound_WhenDentistNotFound() {

        when(dentistRepository.getReferenceById(invalidId)).thenThrow(EntityNotFoundException.class);

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> dentistService.update(updateDentistDTO, invalidId));

        assertEquals("Dentist not found! ID: " + invalidId, exception.getMessage());

        verify(dentistRepository, times(1)).getReferenceById(invalidId);
    }

    @Test
    void update_ShouldThrowDatabaseException_WhenEmailAlreadyInUse() {

        user.setId(999L);

        when(dentistRepository.getReferenceById(validId)).thenReturn(dentist);
        when(userRepository.findByEmail(dentistDTO.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(DatabaseException.class,
                () -> dentistService.update(updateDentistDTO, validId));

        assertEquals("Email already exists.", exception.getMessage());
        verify(dentistRepository, times(1)).getReferenceById(validId);
        verify(userRepository, times(1)).findByEmail(dentistDTO.getEmail());
    }

    @Test
    void deleteById_ShouldDoNothing_WhenDentistExists() {

        when(dentistRepository.existsById(validId)).thenReturn(true);

        dentistService.deleteById(validId);

        verify(dentistRepository, times(1)).existsById(validId);
        verify(dentistRepository).deleteById(validId);
    }

    @Test
    void deleteById_ShouldThrowResourceNotFound_WhenDentistNotFound() {

        when(dentistRepository.existsById(invalidId)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> dentistService.deleteById(invalidId));

        verify(dentistRepository, times(1)).existsById(invalidId);
        assertEquals("Dentist not found! ID: " + invalidId, exception.getMessage());
    }

    @Test
    void deleteById_ShouldThrowDatabaseException_WhenIntegrityViolation() {

        when(dentistRepository.existsById(dependentId)).thenReturn(true);

        doThrow(DataIntegrityViolationException.class).when(dentistRepository).deleteById(dependentId);

        Exception exception = assertThrows(DatabaseException.class, () -> dentistService.deleteById(dependentId));

        verify(dentistRepository, times(1)).existsById(dependentId);
        assertEquals("Database error: Data integrity rules were violated.", exception.getMessage());
    }
}
