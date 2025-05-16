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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.dental_care.dto.CreateDentistDTO;
import br.com.dental_care.dto.DentistChangePasswordDTO;
import br.com.dental_care.dto.DentistDTO;
import br.com.dental_care.dto.DentistMinDTO;
import br.com.dental_care.dto.UpdateDentistDTO;
import br.com.dental_care.service.DentistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dentists")
@RequiredArgsConstructor
public class DentistController {

    private final DentistService dentistService;
    private final Logger logger = LoggerFactory.getLogger(DentistController.class);

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')")
    public ResponseEntity<DentistDTO> findById(@PathVariable Long id) {
        logger.info("Searching dentist with id: {}", id);
        DentistDTO dto = dentistService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')")
    public ResponseEntity<Page<DentistMinDTO>> findAll(Pageable pageable) {
        logger.info("Searching all dentists");

        Page<DentistMinDTO> dto = dentistService.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DentistDTO> insert(@Valid @RequestBody CreateDentistDTO dto) {
        logger.info("Creating new dentist with email: {}", dto.getEmail());
        DentistDTO response = dentistService.save(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DENTIST')")
    public ResponseEntity<DentistDTO> update(@Valid @RequestBody UpdateDentistDTO dto, @PathVariable Long id) {
        logger.info("Updating dentist with id: {}, email: {}", id, dto.getEmail());
        DentistDTO response = dentistService.update(dto, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/change-password")
    @PreAuthorize("hasRole('ROLE_DENTIST')")
    public ResponseEntity<Void> changePassword(@RequestBody DentistChangePasswordDTO dto) {
        logger.info("Updating dentist password with id: {}", dto.getUsername());
        dentistService.changePassword(dto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        logger.info("Deleting dentist with id: {}", id);
        dentistService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
