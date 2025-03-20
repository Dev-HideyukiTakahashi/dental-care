package br.com.dental_care.controller;

import br.com.dental_care.dto.DentistDTO;
import br.com.dental_care.dto.DentistMinDTO;
import br.com.dental_care.exception.handler.CustomError;
import br.com.dental_care.service.DentistService;
import br.com.dental_care.utils.PaginationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/dentists")
@RequiredArgsConstructor
@Tag(name = "Dentist", description = "Endpoints for managing dentists")
public class DentistController {

    private final DentistService dentistService;
    private final Logger logger = LoggerFactory.getLogger(DentistController.class);

    @Operation(summary = "Find dentist by ID", description = "Find a dentist by their unique ID.", tags = "Dentist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dentist found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DentistDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Dentist not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<DentistDTO> findById(@PathVariable Long id) {
        logger.info("Searching dentist with id: {}", id);
        DentistDTO dto = dentistService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Find all dentists", description = "Retrieve all dentists in a paginated format.", tags = "Dentist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dentists found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<DentistMinDTO>> findAll(
             @RequestParam(defaultValue = "0")
             @Parameter(description = "Page number", example = "0") int page,
             @RequestParam(defaultValue = "10")
             @Parameter(description = "Page size", example = "10") int size,
             @RequestParam(defaultValue = "id,asc")
             @Parameter(description = "Sorting criteria", example = "id,asc") String sort) {

        Pageable pageable = PaginationUtils.buildPageable(page, size, sort);
        logger.info("Searching all dentists");
        Page<DentistMinDTO> dto = dentistService.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Create a new dentist", description = "Create a new dentist with the provided details.", tags = "Dentist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dentist created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DentistDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @PostMapping
    public ResponseEntity<DentistDTO> insert(@Valid @RequestBody DentistDTO dto) {
        logger.info("Creating new dentist with email: {}", dto.getEmail());
        dto = dentistService.save(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Update an existing dentist", description = "Update an existing dentist by their ID.", tags = "Dentist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dentist updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DentistDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Dentist not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @PutMapping(path = "/{id}")
    public ResponseEntity<DentistDTO> update(@Valid @RequestBody DentistDTO dto, @PathVariable Long id) {
        logger.info("Updating dentist with id: {}, email: {}", id, dto.getEmail());
        dto = dentistService.update(dto, id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Delete a dentist by ID", description = "Delete a dentist by their unique ID.", tags = "Dentist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dentist deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Dentist not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        logger.info("Deleting dentist with id: {}", id);
        dentistService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
