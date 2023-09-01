package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.request.NoteInDTO;
import io.renatofreire.passwordmanager.dto.response.NoteOutDTO;
import io.renatofreire.passwordmanager.exception.EntityAlreadyExistsException;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import io.renatofreire.passwordmanager.mapper.NoteMapper;
import io.renatofreire.passwordmanager.model.Note;
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
    private final SecurityService securityService;

    @Autowired
    public NoteService(UserRepository userRepository, NoteRepository noteRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.securityService = securityService;
    }

    public List<NoteOutDTO> getAllNotes(final UserDetails userDetails){
        return noteRepository.findByUserEmail(userDetails.getUsername()).stream()
                .map(note -> {
                    String decodedDescription = securityService.decode(note.getDescription(), note.getUser());
                    return NoteMapper.map(note, decodedDescription);
                })
                .collect(Collectors.toList());
    }

    public NoteOutDTO getNoteById(final UserDetails userDetails, final Long noteId){
        Optional<Note> note = noteRepository.findByIdAndUserEmail(noteId, userDetails.getUsername());
        if(note.isEmpty()){
            return new NoteOutDTO(null, null, null, null);
        }else{
            Note presentNote = note.get();
            return NoteMapper.map(presentNote, securityService.decode(presentNote.getDescription(), presentNote.getUser()));
        }
    }

    public NoteOutDTO createNewNote(final UserDetails userDetails, final NoteInDTO newNote){
        if( newNote.name() == null || newNote.description() == null){
            throw new InvalidFieldException("Name or description cannot be null");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("User was not found"));

        Optional<Note> note = noteRepository.findByNameAndUserEmail(newNote.name(), userDetails.getUsername());
        if(note.isPresent()){
            throw new EntityAlreadyExistsException();
        }

        byte[] encodedNoteDescription = securityService.encode(newNote.description(), user);
        Note presentNote = new Note(newNote.name(), encodedNoteDescription, user);

        return NoteMapper.map(noteRepository.save(presentNote), newNote.description());
    }

    public NoteOutDTO updateNewNote(final UserDetails userDetails, final Long noteId, final NoteInDTO newNote){
        if( newNote.name() == null || newNote.description() == null){
            throw new InvalidFieldException("Name or description cannot be null");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("User was not found"));

        Optional<Note> note = noteRepository.findByIdAndUserEmail(noteId, userDetails.getUsername());
        if(note.isEmpty()){
            throw new EntityNotFoundException();
        }

        final Note outdatedNote = note.get();

        byte[] encodedNoteDescription = securityService.encode(newNote.description(), user);
        Note updatedNote = new Note(newNote.name(), encodedNoteDescription, user);
        updatedNote.setId(outdatedNote.getId());

        return NoteMapper.map(updatedNote, newNote.description());
    }

    public boolean deleteNewNote(final UserDetails userDetails, final Long noteId){

        Optional<Note> note = noteRepository.findByIdAndUserEmail(noteId, userDetails.getUsername());
        if(note.isEmpty()){
            throw new EntityNotFoundException();
        }

        noteRepository.delete(note.get());

        return true;
    }

}
