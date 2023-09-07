package io.renatofreire.passwordmanager.controller;

import io.renatofreire.passwordmanager.dto.request.NoteInDTO;
import io.renatofreire.passwordmanager.dto.response.NoteOutDTO;
import io.renatofreire.passwordmanager.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<NoteOutDTO>> getAllNotes(@AuthenticationPrincipal final UserDetails userDetails){
        return ResponseEntity.status(HttpStatus.OK).body(noteService.getAllNotes(userDetails));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteOutDTO> getNoteById(@AuthenticationPrincipal final UserDetails userDetails, @PathVariable final Long noteId){
        return ResponseEntity.status(HttpStatus.OK).body(noteService.getNoteById(userDetails, noteId));
    }

    @PostMapping
    public ResponseEntity<NoteOutDTO> createNewNote(@AuthenticationPrincipal final UserDetails userDetails, @RequestBody final NoteInDTO newNote){
        return ResponseEntity.status(HttpStatus.OK).body(noteService.createNewNote(userDetails, newNote));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteOutDTO> updateNote(@AuthenticationPrincipal final UserDetails userDetails, @PathVariable final Long noteId, @RequestBody final NoteInDTO updateNote){
        return ResponseEntity.status(HttpStatus.OK).body(noteService.updateNewNote(userDetails, noteId, updateNote));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Boolean> deleteNote(@AuthenticationPrincipal final UserDetails userDetails, @PathVariable final Long noteId){
        return ResponseEntity.status(HttpStatus.OK).body(noteService.deleteNote(userDetails, noteId));
    }

}
