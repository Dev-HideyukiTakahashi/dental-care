package br.com.dental_care.exception;

public class MethodArgumentNotValidException  extends RuntimeException {
  
  public MethodArgumentNotValidException (String msg){
    super(msg);
  }
}
