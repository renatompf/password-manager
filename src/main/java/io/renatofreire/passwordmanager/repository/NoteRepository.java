package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n WHERE n.name = :name AND n.user.email = :userEmail")
    Optional<Note> findByNameAndUserEmail(String name, String userEmail);

    @Query("SELECT n FROM Note n WHERE n.user.email = :userEmail")
    List<Note> findByUserEmail(String userEmail);

    @Query("SELECT n FROM Note n WHERE n.id = :noteId AND n.user.email = :userEmail")
    Optional<Note> findByIdAndUserEmail(Long noteId, String userEmail);
}
