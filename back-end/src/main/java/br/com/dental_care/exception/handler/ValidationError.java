package br.com.dental_care.exception.handler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.FieldError;

import lombok.Getter;

@Getter
public class ValidationError extends CustomError {

  ValidationError(Instant timestamp, Integer status, String error, String message, String path) {
    super(timestamp, status, error, message, path);
  }

  private final List<FieldMessage> fields = new ArrayList<>();

  public void addError(FieldError error){
    fields.add(new FieldMessage(error.getField(),error.getDefaultMessage()));
  }
}
