package br.com.dental_care.controller;

import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.exception.handler.CustomError;
import br.com.dental_care.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "Get logged-in patient", description = "Retrieves the currently authenticated patient.",
            tags = "Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomError.class)))
    })
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(path = "/me")
    public ResponseEntity<PatientDTO> getLoggedUser() {
        logger.info("Retrieving details of the authenticated patient");
        PatientDTO dto = userService.getLoggedUser();
        return ResponseEntity.ok(dto);
    }
}
