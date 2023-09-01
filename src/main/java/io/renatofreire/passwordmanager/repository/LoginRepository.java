package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    @Query("SELECT p FROM Login p WHERE p.user.id = :userId")
    List<Login> findByUserId(Integer userId);

    @Query("SELECT p FROM Login p WHERE p.user.email = :email")
    List<Login> findByUserEmail(String email);

    @Query("SELECT p FROM Login p WHERE p.id = :passwordId AND p.user.email = :email")
    Optional<Login> findByIdAndUserEmail(Long passwordId, String email);

    @Query("SELECT p FROM Login p WHERE p.name = :name AND p.user.email = :userEmail")
    Optional<Login> findByNameAndUserEmail(String name, String userEmail);
}
