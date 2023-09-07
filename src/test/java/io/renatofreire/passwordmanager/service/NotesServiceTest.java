package io.renatofreire.passwordmanager.service;



import io.renatofreire.passwordmanager.dto.request.NoteInDTO;
import io.renatofreire.passwordmanager.dto.response.NoteOutDTO;
import io.renatofreire.passwordmanager.exception.EntityAlreadyExistsException;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import io.renatofreire.passwordmanager.mapper.NoteMapper;
import io.renatofreire.passwordmanager.model.Note;
import io.renatofreire.passwordmanager.repository.NoteRepository;
import io.renatofreire.passwordmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

public class NotesServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private EncryptionService encryptionService;

    @Captor
    private ArgumentCaptor<Note> noteArgumentCaptor;

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noteService = new NoteService(userRepository, noteRepository, encryptionService);
    }

    @Test
    void itShouldGetNoteById(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Note savedNote = new Note(1L, "name", "description".getBytes(), user);

        NoteOutDTO note = NoteMapper.map(savedNote, "decription");

        // When
        when(noteRepository.findByIdAndUserEmail(savedNote.getId(), userDetails.getUsername())).thenReturn(Optional.of(savedNote));
        when(encryptionService.decode(savedNote.getDescription(), user)).thenReturn("decription");

        // Then
        NoteOutDTO result = noteService.getNoteById(userDetails, 1L);
        assertThat(result).usingRecursiveComparison().isEqualTo(note);
    }

    @Test
    void itShouldGetNotNoteByIdIfNoteNotFound(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));


        // When
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        NoteOutDTO result = noteService.getNoteById(userDetails, 1L);
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(new NoteOutDTO(null, null, null, null));
    }

    @Test
    void itShouldCreateNewNote(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        NoteInDTO note = new NoteInDTO("name", "decription");

        Note savedNote = new Note(1L, "name", "description".getBytes(), user);


        // When
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(noteRepository.findByNameAndUserEmail(savedNote.getName(),userDetails.getUsername())).thenReturn(Optional.empty());

        byte[] encodedDescription = "description".getBytes();
        when(encryptionService.encode(note.description(), user)).thenReturn(encodedDescription);
        when(noteRepository.save(noteArgumentCaptor.capture())).thenReturn(savedNote);

        NoteOutDTO finalResult = noteService.createNewNote(userDetails, note);

        // Then
        then(noteRepository).should().save(noteArgumentCaptor.capture());
        Note result = noteArgumentCaptor.getValue();
        assertThat(new NoteInDTO(result.getName(), "decription"))
                .usingRecursiveComparison()
                .isEqualTo(finalResult);

    }

    @Test
    void itShouldNotCreateNewNoteIfSomeElementOfTheBodyIsNull(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        NoteInDTO note = new NoteInDTO(null, "decription");

        // When
        // Then
        assertThatThrownBy(() -> noteService.createNewNote(userDetails, note))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessageContaining("Name or description cannot be null");
        then(noteRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotCreateNewNoteIfUserNotFound(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        NoteInDTO note = new NoteInDTO("name", "decription");

        // When
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());


        // Then
        assertThatThrownBy(() -> noteService.createNewNote(userDetails, note))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User was not found");
        then(noteRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotCreateNewNoteIfNoteAlreadyExists(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        NoteInDTO note = new NoteInDTO("name", "decription");

        Note savedNote = new Note(1L, "name", "description".getBytes(), user);


        // When
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(noteRepository.findByNameAndUserEmail(savedNote.getName(),userDetails.getUsername())).thenReturn(Optional.of(savedNote));


        // Then
        assertThatThrownBy(() -> noteService.createNewNote(userDetails, note))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    void itShouldUpdateNote(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        NoteInDTO note = new NoteInDTO("newname", "newdecription");

        Note savedNote = new Note(1L, "name", "description".getBytes(), user);

        // When
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(noteRepository.findByIdAndUserEmail(1L,userDetails.getUsername())).thenReturn(Optional.of(savedNote));

        byte[] encodedDescription = "newdecription".getBytes();
        when(encryptionService.encode(note.description(), user)).thenReturn(encodedDescription);
        when(noteRepository.save(noteArgumentCaptor.capture())).thenReturn(savedNote);

        NoteOutDTO finalResult = noteService.updateNewNote(userDetails, 1L ,note);

        // Then
        then(noteRepository).should().save(noteArgumentCaptor.capture());
        Note result = noteArgumentCaptor.getValue();
        assertThat(new NoteOutDTO(result.getId(), result.getName(), "newdecription", result.getUser().getId()))
                .usingRecursiveComparison()
                .isEqualTo(finalResult);

    }

    @Test
    void itShouldNotUpdateNewNoteIfSomeElementOfTheBodyIsNull(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        NoteInDTO note = new NoteInDTO(null, "decription");

        // When
        // Then
        assertThatThrownBy(() -> noteService.updateNewNote(userDetails,1L, note))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessageContaining("Name or description cannot be null");
        then(noteRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotUpdateNewNoteIfUserNotFound(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        NoteInDTO note = new NoteInDTO("name", "decription");

        // When
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> noteService.updateNewNote(userDetails,1L, note))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User was not found");
        then(noteRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotUpdateNewNoteIfNoteDoesNotExists(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        NoteInDTO note = new NoteInDTO("name", "decription");

        // When
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(noteRepository.findByIdAndUserEmail(1L,userDetails.getUsername())).thenReturn(Optional.empty());


        // Then
        assertThatThrownBy(() -> noteService.updateNewNote(userDetails, 1L, note))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void itShouldDeleteNote(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Note savedNote = new Note(1L, "name", "description".getBytes(), user);

        // When
        when(noteRepository.findByIdAndUserEmail(1L,userDetails.getUsername())).thenReturn(Optional.of(savedNote));

        // Then
        boolean result = noteService.deleteNote(userDetails, 1L);
        assertThat(result).isTrue();
        then(noteRepository).should().delete(any(Note.class));

    }

    @Test
    void itShouldNotDeleteNoteIfNotFound(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));


        // When
        // Then
        assertThatThrownBy(() -> noteService.deleteNote(userDetails,1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

}

