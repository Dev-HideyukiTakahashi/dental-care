package br.com.dental_care.repository;

import br.com.dental_care.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDateBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
