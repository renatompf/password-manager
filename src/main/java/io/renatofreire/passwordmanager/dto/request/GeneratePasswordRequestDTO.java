package io.renatofreire.passwordmanager.dto.request;

public record GeneratePasswordRequestDTO(int length,
                                         boolean useLower,
                                         boolean useUpper,
                                         boolean useNumber,
                                         boolean useSpecials) {
}
