package br.com.dental_care.exception;

public class InvalidRatingDataException extends RuntimeException {

  public InvalidRatingDataException(String msg){
    super(msg);
  }
}
