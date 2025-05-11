package br.com.dental_care.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;

import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.factory.PatientFactory;
import br.com.dental_care.factory.UserFactory;
import br.com.dental_care.model.User;
import br.com.dental_care.projection.UserDetailsProjection;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    public void loadUserByUsername_Should_returnUserDetails_When_userExists() {

        // Arrange
        String validUser = "test@example.com";

        UserDetailsProjection projectionMock = mock(UserDetailsProjection.class);
        when(projectionMock.getRoleId()).thenReturn(1L);
        when(projectionMock.getAuthority()).thenReturn("ROLE_USER");
        when(projectionMock.getPassword()).thenReturn("encodedPassword");
        List<UserDetailsProjection> list = List.of(projectionMock);

        when(userRepository.searchUserAndRolesByEmail(validUser)).thenReturn(list);

        // Act
        UserDetails details = userService.loadUserByUsername(validUser);

        // Assert
        assertNotNull(details);
        assertEquals(validUser, details.getUsername());
        assertEquals("encodedPassword", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void loadUserByUsername_Should_throwException_When_userNotFound() {

        String invalidUser = "invalid.test@example.com";

        when(userRepository.searchUserAndRolesByEmail(invalidUser)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(invalidUser);
        });

        assertEquals("User not found!", exception.getMessage());
        verify(userRepository, times(1)).searchUserAndRolesByEmail(invalidUser);
    }

    @Test
    public void getLoggedPatient_Should_returnPatientDTO_When_authenticatedPatientExists() {

        String validUser = "john.doe@example.com";
        User user = UserFactory.createValidUser();

        // Mock JWT and Spring Security context
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("username")).thenReturn(validUser);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Mocks repositories
        when(userRepository.findByEmail(validUser)).thenReturn(Optional.of(user));
        when(patientRepository.getReferenceById(user.getId()))
                .thenReturn(PatientFactory.createValidPatient());

        // Act
        PatientDTO dto = userService.getLoggedPatient();

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getEmail());
        assertNotNull(dto.getName());
        assertEquals(validUser, dto.getEmail());
        assertEquals("John Doe", dto.getName());
        assertEquals("(11) 99710-2376", dto.getPhone());
    }

    @Test
    public void getLoggedUser_Should_returnPatientDTO_When_authenticatedUserExists() {

        String validUser = "test@example.com";
        User user = UserFactory.createValidUser();

        // Mock JWT and Spring Security context
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("username")).thenReturn(validUser);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Mocks repositories
        when(userRepository.findByEmail(validUser)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getLoggedUser();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getEmail());
        assertNotNull(result.getName());
        assertEquals(validUser, result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals("11912345678", result.getPhone());
    }

    @Test
    public void authenticated_Should_returnUser_When_validJwt() {

        // Arrange
        String userEmail = "test@example.com";

        // Mock the Jwt, extracting the "username" claim from the JWT
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("username")).thenReturn(userEmail);

        // Mock the user repository, returning a valid user
        User user = UserFactory.createValidUser();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Mock the authentication and security context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        User result = userService.authenticated();

        // Assert
        assertNotNull(result);
        assertEquals(userEmail, result.getEmail());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    public void authenticated_Should_throwException_When_anyErrorOccurs() {

        // Mock the Jwt, simulating a failure when retrieving the claim
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("username")).thenThrow(new UsernameNotFoundException("Email not found"));

        // Mock the authentication and security context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        RuntimeException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.authenticated();
        });

        // Assert
        assertEquals("Email not found", exception.getMessage());
    }
}
