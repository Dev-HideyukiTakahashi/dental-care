package br.com.dental_care.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_dentist")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Dentist extends User {

  private String speciality;
  private String registrationNumber;


  
}
