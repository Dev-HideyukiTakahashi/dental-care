package br.com.dental_care.repository;

import br.com.dental_care.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<Patient, Long> {

   @Query(value = "SELECT obj FROM Patient obj JOIN FETCH obj.roles",
           countQuery = "SELECT COUNT(obj) FROM Patient obj JOIN obj.roles")
   Page<Patient> searchAll(Pageable pageable);
}
