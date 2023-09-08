package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId and (t.expired = false or t.revoke = false)")
    List<Token> findAllValidTokensByUser(Integer userId);

    Optional<Token> findByToken(String token);



}
