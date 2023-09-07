package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.Note;
import io.renatofreire.passwordmanager.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @AfterEach
    public void cleanup() {
        noteRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void ShouldSelectNoteByNameAndUserEmail(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String noteName = "note name";
        Note note = new Note(1L, noteName, "description".getBytes(), user);

        // Then
        userRepository.save(user);
        noteRepository.save(note);

        // When
        Optional<Note> result = noteRepository.findByNameAndUserEmail(noteName, email);
        assertThat(result)
                .isPresent()
                .hasValueSatisfying(n -> assertThat(note).usingRecursiveComparison().isEqualTo(note));
    }

    @Test
    void ShouldNotSelectNoteByNameAndUserEmailIfNoteWithNameDoesNotExist(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String fakeName = "fake name";
        Note note = new Note(1L, "note name", "description".getBytes(), user);

        // Then
        userRepository.save(user);
        noteRepository.save(note);

        // When
        Optional<Note> result = noteRepository.findByNameAndUserEmail(fakeName, email);
        assertThat(result)
                .isNotPresent();
    }

    @Test
    void ShouldNotSelectNoteByNameAndUserEmailIfNoteWithUserEmailDoesNotExist(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String fakeEmail = "fake@email.com";
        String noteName = "note name";
        Note note = new Note(1L, noteName, "description".getBytes(), user);

        // Then
        userRepository.save(user);
        noteRepository.save(note);

        // When
        Optional<Note> result = noteRepository.findByNameAndUserEmail(noteName, fakeEmail);
        assertThat(result)
                .isNotPresent();
    }

    @Test
    void ShouldSelectNotesByUserEmail() {
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        Note note = new Note(1L, "note name", "description".getBytes(), user);
        List<Note> expected = List.of(note);

        // Then
        userRepository.save(user);
        noteRepository.save(note);

        // When
        List<Note> result = noteRepository.findByUserEmail(email);
        assertThat(result)
                .isNotEmpty()
                .hasSize(expected.size());
    }

    @Test
    void ShouldNotSelectNotesByUserEmailIfNotesForThatUserDoesNotExist(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String fakeEmail = "fake@email.com";

        Note note = new Note(1L, "note name", "description".getBytes(), user);

        // Then
        userRepository.save(user);
        noteRepository.save(note);

        // When
        List<Note> result = noteRepository.findByUserEmail(fakeEmail);
        assertThat(result)
                .isEmpty();
    }

}
