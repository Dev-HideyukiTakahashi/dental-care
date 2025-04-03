package br.com.dental_care.repository;

import br.com.dental_care.model.User;
import br.com.dental_care.projection.UserDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
  
  @Query(value = "SELECT obj FROM tb_user obj JOIN FETCH obj.roles",
          countQuery = "SELECT COUNT(obj) FROM tb_user obj JOIN obj.roles")
  Page<User> searchAll(Pageable pageable);

  @Query(nativeQuery = true, value = """
    SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
    FROM tb_user
    INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
    INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
    WHERE tb_user.email = :email
  """)
  List<UserDetailsProjection> searchUserAndRolesByEmail(String email);
}
