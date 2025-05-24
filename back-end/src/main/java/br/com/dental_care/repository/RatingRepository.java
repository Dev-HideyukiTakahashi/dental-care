package br.com.dental_care.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.dental_care.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByAppointment_Id(Long id);
}
