package br.com.dental_care.exception;

public class ScheduleConflictException extends RuntimeException {

  public ScheduleConflictException(String msg){
    super(msg);
  }
}
