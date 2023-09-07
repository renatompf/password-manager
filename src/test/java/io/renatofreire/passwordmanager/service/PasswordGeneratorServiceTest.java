package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.request.GeneratePasswordRequestDTO;
import io.renatofreire.passwordmanager.exception.BodyIsMissingException;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PasswordGeneratorServiceTest {

    @Mock
    private SecureRandom random;
    private final CharSequence upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final CharSequence lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
    private final CharSequence specialCharacters = "@#$€%&({[]})<>";
    private final CharSequence numbers = "1234567890";

    private PasswordGeneratorService passwordGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordGeneratorService = new PasswordGeneratorService();
    }

    @Test
    void itShouldNotGenerateRandomPasswordIfRequestIsNull(){
        // Given
        GeneratePasswordRequestDTO passwordFields = null;

        // Then
        // When
        assertThatThrownBy(() -> passwordGeneratorService.generatePassword(passwordFields))
                .isInstanceOf(BodyIsMissingException.class)
                .hasMessage("Body to generate new password is missing");
    }

    @Test
    void itShouldNotGenerateRandomPasswordIfLengthIsNegative(){
        // Given
        GeneratePasswordRequestDTO passwordFields = new GeneratePasswordRequestDTO(-1, true, true, true,  true);

        // Then
        // When
        assertThatThrownBy(() -> passwordGeneratorService.generatePassword(passwordFields))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Length cannot be negative");
    }

    @Test
    void itShouldNotGenerateRandomPasswordIfLengthILongerThan128(){
        // Given
        GeneratePasswordRequestDTO passwordFields = new GeneratePasswordRequestDTO(129, true, true, true, true);

        // Then
        // When
        assertThatThrownBy(() -> passwordGeneratorService.generatePassword(passwordFields))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Length cannot be longer than 128");
    }

    @Test
    void itShouldGenerateRandomPassword(){
        // Given
        GeneratePasswordRequestDTO passwordFields = new GeneratePasswordRequestDTO(19, true, true, true, true);


        // Then
        // When
        String result = passwordGeneratorService.generatePassword(passwordFields);
        assertThat(result)
                .hasSize(passwordFields.length());
    }

}


