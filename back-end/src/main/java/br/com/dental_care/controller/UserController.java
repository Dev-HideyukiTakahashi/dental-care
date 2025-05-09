package br.com.dental_care.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(path = "/me")
    public ResponseEntity<PatientDTO> getLoggedUser() {
        logger.info("Retrieving details of the authenticated patient");
        PatientDTO dto = userService.getLoggedPatient();
        return ResponseEntity.ok(dto);
    }
}
