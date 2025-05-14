package br.com.dental_care.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dental_care.dto.CreatePatientDTO;
import br.com.dental_care.dto.EmailDTO;
import br.com.dental_care.dto.NewPasswordDTO;
import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<PatientDTO> registerPatient(@Valid @RequestBody CreatePatientDTO dto) {
        logger.info("Initiating patient registration process");
        PatientDTO response = authService.registerPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDTO body) {
        logger.info("Starting token generation process for password recovery");
        authService.createRecoverToken(body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto) {
        logger.info("Initiating reset password process");
        authService.saveNewPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
