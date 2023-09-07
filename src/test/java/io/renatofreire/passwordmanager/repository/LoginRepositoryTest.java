package io.renatofreire.passwordmanager.repository;

import io.renatofreire.passwordmanager.model.Login;
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
public class LoginRepositoryTest {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @AfterEach
    public void cleanup() {
        loginRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void itShouldSelectLoginsByUserEmail(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        Login firstLogin = new Login(1L, "login 1", "username", "password".getBytes(), "url", "description", user);
        List<Login> expected = List.of(
                firstLogin
        );

        // Then
        userRepository.save(user);
        loginRepository.save(firstLogin);

        // When
        List<Login> result = loginRepository.findByUserEmail(email);
        assertThat(result)
                .isNotEmpty()
                .hasSize(expected.size());

    }

    @Test
    void itShouldNotSelectLoginsByUserEmailIfUserWithEmailDoesNotExist(){
        // Given
        String searchEmail = "searchEmail@email.com";

        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        Login firstLogin = new Login(1L, "login 1", "username", "password".getBytes(), "url", "description", user);


        // Then
        userRepository.save(user);
        loginRepository.save(firstLogin);

        // When
        List<Login> result = loginRepository.findByUserEmail(searchEmail);
        assertThat(result).isEmpty();
    }


    @Test
    void itShouldSelectLoginByUserEmailAndLoginName(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String loginName = "login 1";
        Login expected = new Login(4L, loginName, "username", "password".getBytes(), "url", "description", user);

        // Then
        userRepository.save(user);
        loginRepository.save(expected);

        // When
        Optional<Login> result = loginRepository.findByNameAndUserEmail(loginName, email);
        assertThat(result)
                .isPresent()
                .hasValueSatisfying(login -> assertThat(login).usingRecursiveComparison().isEqualTo(expected));

    }

    @Test
    void itShouldNotSelectLoginByNameUserEmailIfLoginWithThatNameDoesNotExist(){
        // Given
        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String loginName = "login 2";
        Login expected = new Login(1L, "login 1", "username", "password".getBytes(), "url", "description", user);

        // Then
        userRepository.save(user);
        loginRepository.save(expected);

        // When
        Optional<Login> result = loginRepository.findByNameAndUserEmail(loginName, email);
        assertThat(result)
                .isNotPresent();
    }

    @Test
    void itShouldNotSelectLoginByNameUserEmailIfLoginWithThatUserEmailDoesNotExist(){
        // Given
        String emailDontExist = "email2@email.com";

        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String loginName = "login 1";
        Login expected = new Login(1L, loginName, "username", "password".getBytes(), "url", "description", user);

        // Then
        userRepository.save(user);
        loginRepository.save(expected);

        // When
        Optional<Login> result = loginRepository.findByNameAndUserEmail(loginName, emailDontExist);
        assertThat(result)
                .isNotPresent();
    }

    @Test
    void itShouldNotSelectLoginByNameUserEmailIfLoginWithThatNameAndUserEmailDoesNotExist(){
        // Given
        String emailDontExist = "email2@email.com";

        String email = "email@email.com";
        byte[] keys = "keys".getBytes();
        User user = new User("John", "John", email, "password", keys, keys);

        String loginName = "login 2";
        Login expected = new Login(1L, "login 1", "username", "password".getBytes(), "url", "description", user);

        // Then
        userRepository.save(user);
        loginRepository.save(expected);

        // When
        Optional<Login> result = loginRepository.findByNameAndUserEmail(loginName, emailDontExist);
        assertThat(result)
                .isNotPresent();
    }

}
