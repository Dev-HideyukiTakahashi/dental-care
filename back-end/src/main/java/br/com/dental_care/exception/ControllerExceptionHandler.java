package br.com.dental_care.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.com.dental_care.dto.CustomError;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CustomError> handleNotFound(ResourceNotFoundException e, WebRequest request){
    
    CustomError customError = CustomError
              .builder()
              .timestamp(Instant.now())
              .status(HttpStatus.NOT_FOUND.value())
              .error("Not found")
              .message(e.getMessage())
              .path(request.getDescription(false))
              .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(customError);
  }
  
}
