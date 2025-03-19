package br.com.dental_care.model.enums;

public enum Status {

  SCHEDULED("Scheduled"), 
  CANCELLED("Cancelled"), 
  COMPLETED("Completed");

  private final String value;

  Status(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
  
}
