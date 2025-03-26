package br.com.dental_care.exception.handler;

import java.time.Instant;

import br.com.dental_care.exception.ScheduleConflictException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.com.dental_care.exception.DatabaseException;
import br.com.dental_care.exception.ResourceNotFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CustomError> handleNotFound(ResourceNotFoundException e, WebRequest request){
    
    CustomError customError = CustomError
              .builder()
              .timestamp(Instant.now())
              .status(HttpStatus.NOT_FOUND.value())
              .error(e.getMessage())
              .message("Not Found Error")
              .path(request.getDescription(false))
              .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(customError);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> handleValidation(MethodArgumentNotValidException e, 
                                                                WebRequest request){
    ValidationError customError = new ValidationError(
                      Instant.now(), 
                      HttpStatus.UNPROCESSABLE_ENTITY.value(), 
                      e.getDetailMessageCode(), 
                      "Validation Error in Fields",
                      request.getDescription(false));
    e.getFieldErrors().forEach(error -> customError.addError(error));
    
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(customError);
  }

  @ExceptionHandler(DatabaseException.class)
  public ResponseEntity<CustomError> handleDatabase(DatabaseException e, WebRequest request){
    
    CustomError customError = CustomError
              .builder()
              .timestamp(Instant.now())
              .status(HttpStatus.CONFLICT.value())
              .error(e.getMessage())
              .message("Conflict Error")
              .path(request.getDescription(false))
              .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(customError);
  }

  @ExceptionHandler(InvalidDataAccessApiUsageException.class)
  public ResponseEntity<CustomError> handleInvalidDataAccessApiUsage(InvalidDataAccessApiUsageException e,
                                                                                WebRequest request){
    CustomError customError = CustomError
              .builder()
              .timestamp(Instant.now())
              .status(HttpStatus.BAD_REQUEST.value())
              .error(e.getMessage())
              .message("Bad Request")
              .path(request.getDescription(false))
              .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customError);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<CustomError> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                     WebRequest request){
    CustomError customError = CustomError
            .builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(e.getMessage())
            .message("Bad Request")
            .path(request.getDescription(false))
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customError);
  }

  @ExceptionHandler(ScheduleConflictException.class)
  public ResponseEntity<CustomError> handleHttpMessageNotReadable(ScheduleConflictException e,
                                                                  WebRequest request){
    CustomError customError = CustomError
            .builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error(e.getMessage())
            .message("Conflict in data of schedule")
            .path(request.getDescription(false))
            .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(customError);
  }
  
}
