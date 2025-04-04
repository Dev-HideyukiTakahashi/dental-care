package br.com.dental_care.exception;

public class AccessDeniedException extends RuntimeException {

  public AccessDeniedException(String msg){
    super(msg);
  }
}
