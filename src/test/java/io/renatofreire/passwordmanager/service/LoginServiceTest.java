package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.request.LoginInDTO;
import io.renatofreire.passwordmanager.dto.response.LoginOutDTO;
import io.renatofreire.passwordmanager.exception.EntityAlreadyExistsException;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import io.renatofreire.passwordmanager.mapper.LoginMapper;
import io.renatofreire.passwordmanager.model.Login;
import io.renatofreire.passwordmanager.repository.LoginRepository;
import io.renatofreire.passwordmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

public class LoginServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoginRepository loginRepository;
    @Mock
    private EncryptionService encryptionService;

    @Captor
    private ArgumentCaptor<Login> loginArgumentCaptor;

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginService(userRepository, loginRepository, encryptionService);
    }

    @Test
    void itShouldCreateNewLogin(){
        // Given
        String email = "email@email.com";

        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO("name", "password", "encodedPassword", "url", "description");

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Login savedLogin = new Login(2L, "name", "password", "encodedPassword".getBytes(), "url", "description", user);

        // When
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(loginRepository.findByNameAndUserEmail(newLogin.name(),userDetails.getUsername())).thenReturn(Optional.empty());

        byte[] encodedPassword = "encodedPassword".getBytes();
        when(encryptionService.encode(newLogin.password(), user)).thenReturn(encodedPassword);
        when(loginRepository.save(loginArgumentCaptor.capture())).thenReturn(savedLogin);

        LoginOutDTO finalResult = loginService.createNewLogin(newLogin, userDetails);

        // Then
        then(loginRepository).should().save(loginArgumentCaptor.capture());
        Login result = loginArgumentCaptor.getValue();
        assertThat(new LoginOutDTO(result.getId(), result.getName(),
                result.getUsername(), "encodedPassword",
                result.getUrl(), result.getDescription(), result.getUser().getId()))
                .usingRecursiveComparison()
                .ignoringFields("passwordId")
                .isEqualTo(finalResult);

    }

    @Test
    void itShouldNotCreateNewLoginIfSomeElementOfTheBodyIsNull(){
        // Given
        String email = "email@email.com";

        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO(null, "password", "encodedPassword", "url", "description");

        // When
        // Then
        assertThatThrownBy(() -> loginService.createNewLogin(newLogin, userDetails))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessageContaining("Name, password or username cannot be null");
        then(loginRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotCreateNewLoginIfUserDoesNotExist(){
        // Given
        String email = "email@email.com";

        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO("name", "password", "encodedPassword", "url", "description");

        // When
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> loginService.createNewLogin(newLogin, userDetails))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User was not found");
        then(loginRepository).shouldHaveNoInteractions();

    }

    @Test
    void itShouldNotCreateNewLoginIfLoginAlreadyExists(){
        // Given
        String email = "email@email.com";

        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO("name", "password", "encodedPassword", "url", "description");

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Login savedLogin = new Login(2L, "name", "password", "encodedPassword".getBytes(), "url", "description", user);

        // When
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(loginRepository.findByNameAndUserEmail(newLogin.name(),userDetails.getUsername())).thenReturn(Optional.of(savedLogin));

        // Then
        assertThatThrownBy(() -> loginService.createNewLogin(newLogin, userDetails))
                .isInstanceOf(EntityAlreadyExistsException.class);

    }

    @Test
    void itShouldUpdateLogin(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO("updatedName", "updatedUsername", "encodedPassword", "url", "description");

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Login savedLogin = new Login(2L, "name", "password", "encodedPassword".getBytes(), "url", "description", user);
        Login updated = new Login(2L, "updatedName", "updatedUsername", "encodedPassword".getBytes(), "url", "description", user);

        // When
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(loginRepository.findById(savedLogin.getId())).thenReturn(Optional.of(savedLogin));
        when(loginRepository.findByNameAndUserEmail(newLogin.name(),userDetails.getUsername())).thenReturn(Optional.empty());

        byte[] encodedPassword = "encodedPassword".getBytes();
        when(encryptionService.encode(newLogin.password(), user)).thenReturn(encodedPassword);
        when(loginRepository.save(loginArgumentCaptor.capture())).thenReturn(updated);

        LoginOutDTO finalResult = loginService.updateLogin(2L, newLogin, userDetails);

        // Then
        then(loginRepository).should().save(loginArgumentCaptor.capture());
        Login result = loginArgumentCaptor.getValue();
        assertThat(new LoginOutDTO(result.getId(), result.getName(),
                result.getUsername(), "encodedPassword",
                result.getUrl(), result.getDescription(), result.getUser().getId()))
                .usingRecursiveComparison()
                .ignoringFields("passwordId")
                .isEqualTo(finalResult);

    }

    @Test
    void itShouldNotUpdateLoginIfBodyHasNullValues(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO(null, null, null, "url", "description");

        // When
        // Then
        assertThatThrownBy(() -> loginService.updateLogin(2L, newLogin, userDetails))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessageContaining("Name, password or username cannot be null");
        then(loginRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotUpdateLoginIfUserNotFound(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO("updatedName", "updatedUsername", "encodedPassword", "url", "description");

        // When
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());


        // Then
        assertThatThrownBy(() -> loginService.updateLogin(2L, newLogin, userDetails))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User was not found");
        then(loginRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotUpdateLoginIfLoginDoesNotExist(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO("updatedName", "updatedUsername", "encodedPassword", "url", "description");

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Login savedLogin = new Login(2L, "name", "password", "encodedPassword".getBytes(), "url", "description", user);

        // When
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(loginRepository.findById(savedLogin.getId())).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> loginService.updateLogin(2L, newLogin, userDetails))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Password was not found");
    }

    @Test
    void itShouldNotUpdateLoginIfUserDoesNotHavePermissions(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        LoginInDTO newLogin = new LoginInDTO("updatedName", "updatedUsername", "encodedPassword", "url", "description");

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        io.renatofreire.passwordmanager.model.User fakeUser =
                new io.renatofreire.passwordmanager.model.User("FakeJohn", "FakeJohn", "fakeEmail@email.com", "password", keys, keys);
        user.setId(2);

        Login savedLogin = new Login(2L, "name", "password", "encodedPassword".getBytes(), "url", "description", user);

        // When
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(fakeUser));
        when(loginRepository.findById(savedLogin.getId())).thenReturn(Optional.of(savedLogin));
        when(loginRepository.findByNameAndUserEmail(newLogin.name(),userDetails.getUsername())).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> loginService.updateLogin(2L, newLogin, userDetails))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User denied");

    }

    @Test
    void itShouldDeleteLogin(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Login savedLogin = new Login(2L, "name", "password", "encodedPassword".getBytes(), "url", "description", user);

        // When
        when(loginRepository.findById(savedLogin.getId())).thenReturn(Optional.of(savedLogin));

        // Then
        boolean result = loginService.deleteLogin(2L, userDetails);
        assertThat(result).isTrue();
        then(loginRepository).should().delete(any(Login.class));
    }

    @Test
    void itShouldNotDeleteLoginIfLoginNotFound(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        // When
        when(loginRepository.findById(2L)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> loginService.deleteLogin(2L, userDetails))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Password was not found");
    }

    @Test
    void itShouldNotDeleteLoginIfNoPermissions(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Login savedLogin = new Login(2L, "name", "password", "encodedPassword".getBytes(), "url", "description", user);

        // When
        savedLogin.getUser().setEmail("foo@bar");
        when(loginRepository.findById(savedLogin.getId())).thenReturn(Optional.of(savedLogin));

        // Then
        assertThatThrownBy(() -> loginService.deleteLogin(2L, userDetails))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User denied");

    }

    @Test
    void itShouldGetLoginById(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        String decodedPassword = "encodedPassword";
        Login savedLogin = new Login(2L, "name", "password", decodedPassword.getBytes(), "url", "description", user);

        LoginOutDTO expectedResult = LoginMapper.map(savedLogin, decodedPassword);

        // When
        when(loginRepository.findById(savedLogin.getId())).thenReturn(Optional.of(savedLogin));
        when(encryptionService.decode(savedLogin.getPassword(), user)).thenReturn(decodedPassword);

        // Then
        LoginOutDTO result = loginService.getById(userDetails, 2L);
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void itShouldNotGetLoginByIdIfLoginNotFound(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));


        // When
        when(loginRepository.findById(2L)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> loginService.getById(userDetails,2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Password was not found");
    }

    @Test
    void itShouldNotGetLoginByIdIfNoPermissions(){
        // Given
        String email = "email@email.com";
        UserDetails userDetails = new User(email, "testpassword", Collections.singletonList(new SimpleGrantedAuthority("USER")));

        byte[] keys = "keys".getBytes();
        io.renatofreire.passwordmanager.model.User user = new io.renatofreire.passwordmanager.model.User("John", "John", email, "password", keys, keys);
        user.setId(1);

        Login savedLogin = new Login(2L, "name", "password", "encodedPassword".getBytes(), "url", "description", user);

        // When
        savedLogin.getUser().setEmail("foo@bar");
        when(loginRepository.findById(savedLogin.getId())).thenReturn(Optional.of(savedLogin));

        // Then
        assertThatThrownBy(() -> loginService.getById(userDetails,2L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User denied");

    }

}
