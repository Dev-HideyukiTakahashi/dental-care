package br.com.dental_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.dental_care.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  
}
