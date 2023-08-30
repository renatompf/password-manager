package io.renatofreire.passwordmanager.dto.request;

public record PasswordInDTO(String name,
                            String username,
                            String password,
                            String url,
                            String description) {
}
