package br.com.dental_care.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_admin")
public class Administrator extends User {
  
}
