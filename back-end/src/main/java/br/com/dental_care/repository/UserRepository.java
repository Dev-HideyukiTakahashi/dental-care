package br.com.dental_care.repository;

import br.com.dental_care.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  
  @Query(value = "SELECT obj FROM tb_user obj JOIN FETCH obj.roles",
          countQuery = "SELECT COUNT(obj) FROM tb_user obj JOIN obj.roles")
  Page<User> searchAll(Pageable pageable);
}
