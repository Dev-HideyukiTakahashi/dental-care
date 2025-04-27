package br.com.dental_care.controller;


import br.com.dental_care.dto.AbsenceDTO;
import br.com.dental_care.dto.AbsenceRequestDTO;
import br.com.dental_care.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @PostMapping("/absences/dentist/{id}")
    @PreAuthorize("hasRole('ROLE_DENTIST')")
    public ResponseEntity<AbsenceDTO> createDentistAbsence
            (@PathVariable Long id, @Valid @RequestBody AbsenceRequestDTO request) {
        logger.info("Creating a new absence schedule for dentist with id: {}", id);
        AbsenceDTO dto = scheduleService.createDentistAbsence(id, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_DENTIST')")
    public ResponseEntity<Void> removeDentistAbsence(@PathVariable Long id) {
        logger.info("Deleting absence schedule for dentist with id: {}", id);
        scheduleService.removeDentistAbsence(id);

        return ResponseEntity.noContent().build();
    }
}
