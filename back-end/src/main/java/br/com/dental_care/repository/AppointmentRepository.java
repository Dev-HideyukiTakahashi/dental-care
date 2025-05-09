package br.com.dental_care.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.dental_care.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDateBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);

    Page<Appointment> findByPatient_Id(Pageable pageable, Long patient_Id);

    Page<Appointment> findByDentist_Id(Pageable pageable, Long dentist_Id);
}
