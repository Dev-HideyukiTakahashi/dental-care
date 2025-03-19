package br.com.dental_care.exception;

public class ForbiddenException extends RuntimeException {
  
  public ForbiddenException(String msg){
    super(msg);
  }
}
