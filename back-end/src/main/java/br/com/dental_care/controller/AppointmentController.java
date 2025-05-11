package br.com.dental_care.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')")
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO dto) {
        logger.info("Creating new appointment");
        dto = appointmentService.createAppointment(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_DENTIST')")
    public ResponseEntity<Page<AppointmentDTO>> findAll(Pageable pageable) {
        logger.info("Searching page of appointments");
        Page<AppointmentDTO> dto = appointmentService.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "/date")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_DENTIST')")
    public ResponseEntity<Page<AppointmentDTO>> findByDate(@RequestParam String date, Pageable pageable) {
        logger.info("Retrieving appointments by date with pagination");
        Page<AppointmentDTO> dto = appointmentService.findByDate(date, pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')")
    public ResponseEntity<AppointmentDTO> findById(@PathVariable Long id) {
        logger.info("Searching appointment with id: {}", id);
        AppointmentDTO dto = appointmentService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(path = "/{id}/cancel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        logger.info("Starting the cancellation process for appointment id: {}", id);
        AppointmentDTO dto = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(path = "/{id}/complete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<AppointmentDTO> completeAppointment(@PathVariable Long id) {
        logger.info("Starting the completion process for appointment id: {}", id);
        AppointmentDTO dto = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')")
    public ResponseEntity<AppointmentDTO> updateAppointmentDateTime(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentDTO dto) {
        logger.info("Starting update date/time process for appointment id: {}", id);
        dto = appointmentService.updateAppointmentDateTime(id, dto);
        return ResponseEntity.ok(dto);
    }
}
