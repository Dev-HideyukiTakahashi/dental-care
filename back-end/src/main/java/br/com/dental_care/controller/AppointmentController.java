package br.com.dental_care.controller;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.service.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment", description = "Endpoints for managing appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO dto){
        logger.info("Creating new appointment");
        dto = appointmentService.createAppointment(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AppointmentDTO> findById(@PathVariable  Long id){
        logger.info("Searching appointment with id: {}", id);
        AppointmentDTO dto = appointmentService.findById(id);
        return ResponseEntity.ok(dto);
    }
}
