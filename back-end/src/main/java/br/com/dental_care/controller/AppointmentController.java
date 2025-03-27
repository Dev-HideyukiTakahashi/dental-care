package br.com.dental_care.controller;

import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.exception.handler.CustomError;
import br.com.dental_care.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a new appointment",
            description = "Create a new appointment and add it to the dentist's schedule.",
            tags = "Appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Dentist or Patient not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @PostMapping
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

    @Operation(summary = "Find appointment by ID",
            description = "Find a appointment by their unique ID.", tags = "Appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<AppointmentDTO> findById(@PathVariable Long id) {
        logger.info("Searching appointment with id: {}", id);
        AppointmentDTO dto = appointmentService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id){
        logger.info("Starting the cancellation process for appointment id: {}", id);
        AppointmentDTO dto = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(dto);
    }
}
