package br.com.dental_care.exception.handler;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomError {

  @Schema(description = "Timestamp when the error occurred", example = "2025-03-26T06:35:17.622Z")
  private Instant timestamp;

  @Schema(description = "HTTP status code", example = "404")
  private int status;

  @Schema(description = "Error message", example = "Resource not found! Id : 1")
  private String error;

  @Schema(description = "Detailed error message", example = "Not Found Error")
  private String message;

  @Schema(description = "The path of the API that caused the error", example = "/api/v1/resource/1")
  private String path;
  
}