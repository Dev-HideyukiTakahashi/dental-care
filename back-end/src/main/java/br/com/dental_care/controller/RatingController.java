package br.com.dental_care.controller;

import br.com.dental_care.dto.RatingDTO;
import br.com.dental_care.exception.handler.CustomError;
import br.com.dental_care.service.RatingService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@Tag(name = "Rating", description = "Endpoints for managing ratings")
public class RatingController {

    private final RatingService ratingService;
    private final Logger logger = LoggerFactory.getLogger(RatingController.class);

    @Operation(
            summary = "Rate a dentist",
            description = "Submit a rating for a dentist, providing feedback based on the service received.",
            tags = "Rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating successfully added.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RatingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "404", description = "Dentist not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "422", description = "Appointment has already been rated or not completed.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<RatingDTO> rateDentist(@Valid @RequestBody RatingDTO dto) {
        logger.info("Starting rating process for dentist ID: {}", dto.getDentistId());
        dto = ratingService.rateDentist(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }
}
