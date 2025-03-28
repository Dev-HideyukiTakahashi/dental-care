package br.com.dental_care.repository;

import br.com.dental_care.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByAppointment_Id(Long id);
}
