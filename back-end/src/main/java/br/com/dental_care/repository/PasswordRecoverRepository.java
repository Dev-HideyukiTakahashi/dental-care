package br.com.dental_care.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.dental_care.model.PasswordRecover;

public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover, Long> {
    @Query("SELECT obj FROM tb_password_recover obj WHERE obj.token = :token AND obj.expiration > :now")
    Optional<PasswordRecover> searchValidTokens(String token, Instant now);
}
