package io.renatofreire.passwordmanager.dto.response;

public record PasswordOutDTO(String name,
                            String username,
                            String password,
                            String url,
                            String description) {
}
