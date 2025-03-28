package br.com.dental_care.controller;

import br.com.dental_care.dto.RatingDTO;
import br.com.dental_care.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
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
