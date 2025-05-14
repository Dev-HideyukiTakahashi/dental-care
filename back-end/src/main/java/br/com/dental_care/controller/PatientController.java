package br.com.dental_care.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.dental_care.dto.CreatePatientDTO;
import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.dto.PatientMinDTO;
import br.com.dental_care.dto.UpdatePatientDTO;
import br.com.dental_care.service.PatientService;
import br.com.dental_care.utils.PaginationUtils;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<PatientDTO> findById(@PathVariable Long id) {
        logger.info("Searching patient with id: {}", id);
        PatientDTO dto = patientService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PatientMinDTO>> findAll(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number", example = "0") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size", example = "10") int size,
            @RequestParam(defaultValue = "id,asc") @Parameter(description = "Sorting criteria", example = "id,asc") String sort) {

        Pageable pageable = PaginationUtils.buildPageable(page, size, sort);
        logger.info("Searching all patients");
        Page<PatientMinDTO> dto = patientService.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<PatientDTO> insert(@Valid @RequestBody CreatePatientDTO dto) {
        logger.info("Creating new patient with email: {}", dto.getEmail());
        PatientDTO response = patientService.save(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')")
    public ResponseEntity<PatientDTO> update(@Valid @RequestBody UpdatePatientDTO dto, @PathVariable Long id) {
        logger.info("Updating patient with id: {}, email: {}", id, dto.getEmail());
        PatientDTO response = patientService.update(dto, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        logger.info("Deleting patient with id: {}", id);
        patientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
