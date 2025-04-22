package br.com.dental_care.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.mapper.PatientMapper;
import br.com.dental_care.model.Role;
import br.com.dental_care.model.User;
import br.com.dental_care.projection.UserDetailsProjection;
import br.com.dental_care.repository.PatientRepository;
import br.com.dental_care.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> list = userRepository.searchUserAndRolesByEmail(username);
        if (list.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        User user = new User();
        user.setEmail(username);
        user.setPassword(list.get(0).getPassword());
        list.forEach(role -> user.addRole(new Role(role.getRoleId(), role.getAuthority())));

        return user;
    }

    @Transactional(readOnly = true)
    public PatientDTO getLoggedUser() {
        User user = authenticated();

        logger.info("Patient details retrieved successfully. ID: {}", user.getId());
        return PatientMapper.toDTO(patientRepository.getReferenceById(user.getId()));
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");

            return userRepository.findByEmail(username).get();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }
}
