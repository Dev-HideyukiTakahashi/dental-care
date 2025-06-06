package br.com.dental_care.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
  private Integer score;

  @OneToMany(mappedBy = "dentist")
  private final List<Rating> ratings = new ArrayList<>();

  @OneToMany(mappedBy = "dentist")
  private final List<Schedule> schedules = new ArrayList<>();

  @OneToMany(mappedBy = "dentist")
  private final List<Appointment> appointments = new ArrayList<>();
}
