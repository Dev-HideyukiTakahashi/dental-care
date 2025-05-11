package br.com.dental_care.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.dental_care.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDateBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);

    Page<Appointment> findByDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT a FROM tb_appointment a WHERE a.dentist.id = :dentistId AND a.date BETWEEN :start AND :end")
    Page<Appointment> findByDentistAndDateBetween(@Param("dentistId") Long dentistId,
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

    @Query("SELECT a FROM tb_appointment a WHERE a.patient.id = :patientId AND a.date BETWEEN :start AND :end")
    Page<Appointment> findByPatientAndDateBetween(@Param("patientId") Long patientId,
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

    Page<Appointment> findByPatient_Id(Pageable pageable, Long patient_Id);

    Page<Appointment> findByDentist_Id(Pageable pageable, Long dentist_Id);
}
