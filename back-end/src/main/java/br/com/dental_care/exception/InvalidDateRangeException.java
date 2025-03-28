package br.com.dental_care.exception;

public class InvalidDateRangeException extends RuntimeException {
  public InvalidDateRangeException(String message) {
    super(message);
  }
}