package io.renatofreire.passwordmanager.mapper;

import io.renatofreire.passwordmanager.model.Notes;
import io.renatofreire.passwordmanager.dto.response.NoteOutDTO;
import io.renatofreire.passwordmanager.dto.request.NoteInDTO;

public class NoteMapper {
    public static NoteOutDTO map(Notes note){
        return new NoteOutDTO(note.getId(),note.getName(), note.getDescription(), note.getUser().getId());
    }

    public static Notes map(NoteInDTO note){
        return new Notes(note.name(), note.description());
    }

}
