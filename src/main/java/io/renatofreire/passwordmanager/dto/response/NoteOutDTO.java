package io.renatofreire.passwordmanager.dto.response;

public record NoteOutDTO(Long noteId,
                         String name,
                         String description,
                         Integer userId) {
}
