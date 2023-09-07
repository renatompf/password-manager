package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.enums.TokenType;
import io.renatofreire.passwordmanager.model.Token;
import io.renatofreire.passwordmanager.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @AfterEach
    public void cleanup() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void itShouldFindTokenByToken(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String tokenValue = "token";
        Token token = new Token(tokenValue, TokenType.BEARER, false, true, user);

        // Then
        userRepository.save(user);
        tokenRepository.save(token);

        // When
        Optional<Token> result = tokenRepository.findByToken(tokenValue);
        assertThat(result)
                .isPresent()
                .hasValueSatisfying(t -> assertThat(t).usingRecursiveComparison().isEqualTo(token));
    }

    @Test
    void itShouldNotFindTokenByTokenIfTokenDoesNotExist(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String notExistingToken = "notExistingToken";
        Token token = new Token("token", TokenType.BEARER, false, true, user);

        // Then
        userRepository.save(user);
        tokenRepository.save(token);

        // When
        Optional<Token> result = tokenRepository.findByToken(notExistingToken);
        assertThat(result)
                .isNotPresent();
    }

    @Test
    void itShouldFindAllValidTokensByUserId(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String tokenValue = "token";
        Token firstToken = new Token(tokenValue, TokenType.BEARER, true, true, user);
        Token secondToken = new Token(tokenValue, TokenType.BEARER, false, false, user);

        List<Token> expected = List.of(firstToken, secondToken);

        // Then
        userRepository.save(user);
        tokenRepository.saveAll(expected);

        // When
        List<Token> result = tokenRepository.findAllValidTokensByUser(user.getId());
        assertThat(result)
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void itShouldNotFindAllValidTokensByUserIdIfAllTokensAreNotValid(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String tokenValue = "token";
        Token token = new Token(tokenValue, TokenType.BEARER, true, true, user);

        // Then
        userRepository.save(user);
        tokenRepository.save(token);

        // When
        List<Token> result = tokenRepository.findAllValidTokensByUser(user.getId());
        assertThat(result)
                .isEmpty();
    }

    @Test
    void itShouldNotFindAllValidTokensByUserIdIfUserDoesNotHaveTokens(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String tokenValue = "token";
        Token token = new Token(tokenValue, TokenType.BEARER, true, true, user);

        // Then
        userRepository.save(user);
        tokenRepository.save(token);

        // When
        List<Token> result = tokenRepository.findAllValidTokensByUser(2);
        assertThat(result)
                .isEmpty();
    }

}
