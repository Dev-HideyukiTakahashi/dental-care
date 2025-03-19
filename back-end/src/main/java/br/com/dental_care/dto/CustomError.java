package br.com.dental_care.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomError {

  private Instant timestamp;
  private Integer status;
  private String error;
  private String message;
  private String path;
  
}