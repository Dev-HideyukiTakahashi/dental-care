package br.com.dental_care.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dental_care.dto.AbsenceDTO;
import br.com.dental_care.dto.AbsenceRequestDTO;
import br.com.dental_care.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @GetMapping(path = "/self")
    @PreAuthorize("hasRole('ROLE_DENTIST')")
    public ResponseEntity<AbsenceDTO> findSelfAbsence() {
        logger.info("Retrieving current or upcoming absence period for the dentist.");
        AbsenceDTO dto = scheduleService.findSelfAbsence();

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_DENTIST')")
    public ResponseEntity<AbsenceDTO> createDentistAbsence(@Valid @RequestBody AbsenceRequestDTO request) {
        logger.info("Creating a new absence schedule for dentist");
        AbsenceDTO dto = scheduleService.createDentistAbsence(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_DENTIST')")
    public ResponseEntity<Void> removeDentistAbsence() {
        logger.info("Initiating removal of dentist's absence schedule.");
        scheduleService.removeDentistAbsence();

        return ResponseEntity.noContent().build();
    }
}
