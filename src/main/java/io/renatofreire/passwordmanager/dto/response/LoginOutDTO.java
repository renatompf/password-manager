package io.renatofreire.passwordmanager.dto.response;

public record LoginOutDTO(Long passwordId ,
                          String name,
                          String username,
                          String password,
                          String url,
                          String description,
                          Integer userId) {
}
