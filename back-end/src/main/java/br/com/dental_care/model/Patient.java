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
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "tb_patient")
public class Patient extends User {

  private String medicalHistory;

  @OneToMany(mappedBy = "patient")
  private final List<Rating> ratings = new ArrayList<>();

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((medicalHistory == null) ? 0 : medicalHistory.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Patient other = (Patient) obj;
    if (medicalHistory == null) {
      if (other.medicalHistory != null)
        return false;
    } else if (!medicalHistory.equals(other.medicalHistory))
      return false;
    return true;
  }
}
