package io.renatofreire.passwordmanager.dto.request;

public record GeneratePasswordRequestDTO(int length,
                                         boolean useLower,
                                         boolean useUpper,
                                         boolean useNumber,
                                         int minimalNumberOfNumbers,
                                         boolean useSpecials,
                                         int minimalNumberOfSpecials) {
}
