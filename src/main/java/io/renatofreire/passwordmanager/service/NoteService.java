package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.request.NoteInDTO;
import io.renatofreire.passwordmanager.dto.response.NoteOutDTO;
import io.renatofreire.passwordmanager.exception.EntityAlreadyExistsException;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import io.renatofreire.passwordmanager.mapper.NoteMapper;
import io.renatofreire.passwordmanager.model.Notes;
import io.renatofreire.passwordmanager.model.User;
import io.renatofreire.passwordmanager.repository.NoteRepository;
import io.renatofreire.passwordmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(UserRepository userRepository, NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    public List<NoteOutDTO> getAllNotes(final UserDetails userDetails){
        return noteRepository.findByUserEmail(userDetails.getUsername()).stream()
                .map(NoteMapper::map)
                .collect(Collectors.toList());
    }

    public NoteOutDTO getNoteById(final UserDetails userDetails, final Long noteId){
        Optional<Notes> note = noteRepository.findByIdAndUserEmail(noteId, userDetails.getUsername());
        return note.map(NoteMapper::map).orElseGet(() -> new NoteOutDTO(null, null, null, null));
    }

    public NoteOutDTO createNewNote(final UserDetails userDetails, final NoteInDTO newNote){
        if( newNote.name() == null || newNote.description() == null){
            throw new InvalidFieldException("Name or description cannot be null");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("User was not found"));

        Optional<Notes> note = noteRepository.findByNameAndUserEmail(newNote.name(), userDetails.getUsername());
        if(note.isPresent()){
            throw new EntityAlreadyExistsException();
        }
        Notes presentNote = NoteMapper.map(newNote);
        presentNote.setUser(user);
        return NoteMapper.map(noteRepository.save(presentNote));
    }

    public NoteOutDTO updateNewNote(final UserDetails userDetails, final Long noteId, final NoteInDTO newNote){
        if( newNote.name() == null || newNote.description() == null){
            throw new InvalidFieldException("Name or description cannot be null");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("User was not found"));

        Optional<Notes> note = noteRepository.findByIdAndUserEmail(noteId, userDetails.getUsername());
        if(note.isEmpty()){
            throw new EntityNotFoundException();
        }

        final Notes outdatedNote = note.get();

        Notes updatedNote = NoteMapper.map(newNote);
        updatedNote.setUser(user);
        updatedNote.setId(outdatedNote.getId());

        return NoteMapper.map(updatedNote);
    }

    public boolean deleteNewNote(final UserDetails userDetails, final Long noteId){

        Optional<Notes> note = noteRepository.findByIdAndUserEmail(noteId, userDetails.getUsername());
        if(note.isEmpty()){
            throw new EntityNotFoundException();
        }

        noteRepository.delete(note.get());

        return true;
    }

}
