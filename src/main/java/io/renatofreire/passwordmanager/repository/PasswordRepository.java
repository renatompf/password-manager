package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
    @Query("SELECT p FROM Password p WHERE p.user.id = :userId")
    List<Password> findByUserId(Integer userId);

    @Query("SELECT p FROM Password p WHERE p.user.email = :email")
    List<Password> findByUserEmail(String email);
}
