package io.renatofreire.passwordmanager.mapper;

import io.renatofreire.passwordmanager.model.Note;
import io.renatofreire.passwordmanager.dto.response.NoteOutDTO;

public class NoteMapper {
    public static NoteOutDTO map(Note note, String decyptedDescription){
        return new NoteOutDTO(note.getId(),note.getName(), decyptedDescription, note.getUser().getId());
    }

}
