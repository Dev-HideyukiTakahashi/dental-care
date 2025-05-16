package br.com.dental_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.dental_care.model.Dentist;

public interface DentistRepository extends JpaRepository<Dentist, Long> {

        @Query(value = "SELECT obj FROM Dentist obj JOIN FETCH obj.roles", countQuery = "SELECT COUNT(obj) FROM Dentist obj JOIN obj.roles")
        Page<Dentist> searchAll(Pageable pageable);

        Dentist findByEmail(String email);
}
