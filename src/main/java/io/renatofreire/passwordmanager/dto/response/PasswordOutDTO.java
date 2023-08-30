package io.renatofreire.passwordmanager.dto.response;

public record PasswordOutDTO(Long passwordId ,
                            String name,
                            String username,
                            String password,
                            String url,
                            String description,
                            Integer userId) {
}
