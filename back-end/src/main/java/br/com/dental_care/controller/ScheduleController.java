package br.com.dental_care.controller;

import br.com.dental_care.dto.AbsenceDTO;
import br.com.dental_care.dto.AbsenceRequestDTO;
import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.dto.ScheduleDTO;
import br.com.dental_care.exception.handler.CustomError;
import br.com.dental_care.service.ScheduleService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "Endpoints for managing schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @Operation(summary = "Schedule a dentist absence",
            description = "Create a new absence schedule for the dentist, " +
                          "marking a period when they will be unavailable to attend appointments.",
            tags = "Schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Absence successfully " +
                                                             "created in Schedule.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AbsenceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Dentist not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "409", description = "Schedule data conflict",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @PostMapping("/absences/dentist/{id}")
    public ResponseEntity<AbsenceDTO> createDentistAbsence
            (@PathVariable Long id, @Valid @RequestBody AbsenceRequestDTO request) {
        logger.info("Creating a new absence schedule for dentist with id: {}", id);
        AbsenceDTO dto = scheduleService.createDentistAbsence(id, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Remove a dentist absence",
            description = "Cancel a previously scheduled absence for the dentist.",
            tags = "Schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Schedule successfully deleted.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Schedule not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "409", description = "Schedule data conflict",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeDentistAbsence(@PathVariable Long id) {
        logger.info("Deleting absence schedule for dentist with id: {}", id);
        scheduleService.removeDentistAbsence(id);

        return ResponseEntity.noContent().build();
    }
}
