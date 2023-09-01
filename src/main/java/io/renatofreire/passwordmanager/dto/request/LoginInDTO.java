package io.renatofreire.passwordmanager.dto.request;

public record LoginInDTO(String name,
                         String username,
                         String password,
                         String url,
                         String description) {
}
