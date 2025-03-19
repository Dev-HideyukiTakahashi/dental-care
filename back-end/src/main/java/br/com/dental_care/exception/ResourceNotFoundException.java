package br.com.dental_care.exception;

public class ResourceNotFoundException extends RuntimeException {
  
  public ResourceNotFoundException(String msg){
    super(msg);
  }
}
