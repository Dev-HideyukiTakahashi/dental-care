package br.com.dental_care.repository;

import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover, Long> {
}
