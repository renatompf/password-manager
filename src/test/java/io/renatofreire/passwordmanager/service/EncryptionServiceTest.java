package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        encryptionService = new EncryptionService();
    }

    @Test
    void itShouldGenerateKeyPairValue(){
        // Given
        // When

        // Then
        HashMap<String, byte[]> result = encryptionService.generateKeyPairValue();

        assertThat(result).isNotNull();
        assertThat(result.containsKey("private")).isTrue();
        assertThat(result.containsKey("public")).isTrue();

        byte[] privateKeyBytes = result.get("private");
        byte[] publicKeyBytes = result.get("public");

        assertThat(privateKeyBytes).isNotNull();
        assertThat(publicKeyBytes).isNotNull();
    }

    @Test
    void itShouldEncodeString(){
        // Given
        HashMap<String, byte[]> keyPair = encryptionService.generateKeyPairValue();
        byte[] publicKey = keyPair.get("public");
        byte[] privateKey = keyPair.get("private");

        String email = "email@email.com";
        User user = new User("John", "John", email, "password", publicKey, privateKey);

        // When

        // Then
        byte[] encoded = encryptionService.encode("Message to Encode", user);
        assertThat(encoded).isInstanceOf(byte[].class);
    }

    @Test
    void itShouldEncodeStringIfKeyPairIsNotInRightFormat(){
        // Given
        byte[] keys = "keys".getBytes();
        String email = "email@email.com";
        User user = new User("John", "John", email, "password", keys, keys);

        // When

        // Then
        byte[] result = encryptionService.encode("Message to Encode", user);
        assertThat(result).isEqualTo(new byte[0]);
    }

    @Test
    void itShouldDecodeString(){
        // Given
        HashMap<String, byte[]> keyPair = encryptionService.generateKeyPairValue();
        byte[] publicKey = keyPair.get("public");
        byte[] privateKey = keyPair.get("private");

        String email = "email@email.com";
        User user = new User("John", "John", email, "password", publicKey, privateKey);

        byte[] encoded = encryptionService.encode("Message to Encode", user);

        // When
        // Then
        String decoded = encryptionService.decode(encoded, user);
        assertThat(decoded)
                .isInstanceOf(String.class)
                .isEqualToIgnoringCase("Message to Encode");
    }

    @Test
    void itShouldNotDecodeIfKeyPairIsNotInRightFormat(){
        // Given
        byte[] keys = "keys".getBytes();
        String email = "email@email.com";
        User user = new User("John", "John", email, "password", keys, keys);

        // When
        // Then
        String result = encryptionService.decode("Message to Encode".getBytes(), user);
        assertThat(result).isEqualTo("");
    }

}
