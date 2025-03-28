package br.com.dental_care.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "tb_rating")
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer score;
  private String comment;

  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
  @CreationTimestamp
  private LocalDateTime date;

  private boolean isRated;

  @ManyToOne()
  private Patient patient;

  @ManyToOne()
  private Dentist dentist;

  @ManyToOne()
  private Appointment appointment;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Rating other = (Rating) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
