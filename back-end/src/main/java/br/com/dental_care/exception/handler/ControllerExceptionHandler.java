package br.com.dental_care.exception.handler;

import br.com.dental_care.exception.*;
import jakarta.mail.AuthenticationFailedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> handleNotFound(ResourceNotFoundException e, HttpServletRequest request) {

        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(e.getMessage())
                .message("Not Found Error")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(customError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidation(MethodArgumentNotValidException e,
                                                            HttpServletRequest request) {
        ValidationError customError = new ValidationError(
                Instant.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                e.getDetailMessageCode(),
                "Validation Error in Fields",
                request.getRequestURI());
        e.getFieldErrors().forEach(error -> customError.addError(error));

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(customError);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> handleDatabase(DatabaseException e, HttpServletRequest request) {

        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(e.getMessage())
                .message("Conflict Error")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(customError);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<CustomError> handleInvalidDataAccessApiUsage(InvalidDataAccessApiUsageException e,
                                                                       HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(e.getMessage())
                .message("Bad Request")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomError> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                    HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(e.getMessage())
                .message("Bad Request")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customError);
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<CustomError> handleScheduleConflict(ScheduleConflictException e,
                                                              HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(e.getMessage())
                .message("Schedule data conflict")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(customError);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<CustomError> handleDateRange(InvalidDateRangeException e,
                                                       HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(e.getMessage())
                .message("Invalid date range")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customError);
    }

    @ExceptionHandler(InvalidRatingDataException.class)
    public ResponseEntity<CustomError> handleInvalidRatingData(InvalidRatingDataException e,
                                                               HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error(e.getMessage())
                .message("Invalid rating data")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(customError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomError> handleAccessDeniedException(AccessDeniedException e,
                                                                   HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(e.getMessage())
                .message("Access Denied")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(customError);
    }

    @ExceptionHandler(RegistrationDataException.class)
    public ResponseEntity<CustomError> handleEmailAlreadyRegisteredException(RegistrationDataException e,
                                                                             HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(e.getMessage())
                .message("Invalid registration data provided.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(customError);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CustomError> handleForbiddenException(ForbiddenException e,
                                                                HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(e.getMessage())
                .message("Forbidden Access")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(customError);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<CustomError> handleEmailException(EmailException e,
                                                            HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(e.getMessage())
                .message("Mail error")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customError);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<CustomError> handleAuthenticationFailedException(AuthenticationFailedException e,
                                                                           HttpServletRequest request) {
        CustomError customError = CustomError
                .builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(e.getMessage())
                .message("Internal email error")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(customError);
    }
}
