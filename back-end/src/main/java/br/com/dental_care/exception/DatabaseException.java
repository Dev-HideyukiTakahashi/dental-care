package br.com.dental_care.exception;

public class DatabaseException extends RuntimeException {
  
  public DatabaseException(String msg){
    super(msg);
  }
}
