package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Notes, Long> {

    @Query("SELECT n FROM Notes n WHERE n.user.id = :userId")
    List<Notes> findByUserId(Integer userId);
}
