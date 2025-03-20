package br.com.dental_care.exception.handler;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldMessage {

  private String fieldName;
  private String message;
}
