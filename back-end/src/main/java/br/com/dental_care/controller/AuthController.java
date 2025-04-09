package br.com.dental_care.controller;

import br.com.dental_care.dto.EmailDTO;
import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints related to user authentication and registration")
public class AuthController {

    private final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<PatientDTO> registerPatient(@Valid @RequestBody PatientDTO dto) {
        logger.info("Initiating patient registration process");
        dto = authService.registerPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDTO body) {
        logger.info("Starting token generation process for password recovery");
        authService.createRecoverToken(body);
        return ResponseEntity.noContent().build();
    }
}
