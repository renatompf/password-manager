package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void itShouldSelectUserByEmail(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        // When
        userRepository.save(user);

        // Then
        Optional<User> optionalUser = userRepository.findByEmail(email);
        assertThat(optionalUser)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).usingRecursiveComparison().isEqualTo(user));
    }

    @Test
    void itNotShouldSelectUserByEmailWhenEmailDoesNotExist(){
        // Given
        String email = "email@email.com";

        // When
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Then

        assertThat(optionalUser)
                .isNotPresent();
    }


}
