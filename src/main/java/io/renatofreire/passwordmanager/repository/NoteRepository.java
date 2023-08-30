package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Notes, Long> {

    @Query("SELECT n FROM Notes n WHERE n.user.id = :userId")
    List<Notes> findByUserId(Integer userId);

    @Query("SELECT n FROM Notes n WHERE n.name = :name AND n.user.email = :userEmail")
    Optional<Notes> findByNameAndUserEmail(String name, String userEmail);

    @Query("SELECT n FROM Notes n WHERE n.user.email = :userEmail")
    List<Notes> findByUserEmail(String userEmail);

    @Query("SELECT n FROM Notes n WHERE n.id = :noteId AND n.user.email = :userEmail")
    Optional<Notes> findByIdAndUserEmail(Long noteId, String userEmail);
}
