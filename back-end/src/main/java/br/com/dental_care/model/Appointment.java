package br.com.dental_care.model;

import java.time.LocalDateTime;

import br.com.dental_care.model.enums.AppointmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity(name = "tb_appointment")
public class Appointment {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
  private LocalDateTime date;

  @Enumerated(EnumType.STRING)
  private AppointmentStatus status;

  private String description;

  @ManyToOne
  private Dentist dentist;

  @ManyToOne
  private Patient patient;
  
}
