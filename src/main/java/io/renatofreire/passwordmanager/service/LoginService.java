package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.request.LoginInDTO;
import io.renatofreire.passwordmanager.dto.response.LoginOutDTO;
import io.renatofreire.passwordmanager.exception.EntityAlreadyExistsException;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import io.renatofreire.passwordmanager.mapper.LoginMapper;
import io.renatofreire.passwordmanager.model.Login;
import io.renatofreire.passwordmanager.model.User;
import io.renatofreire.passwordmanager.repository.LoginRepository;
import io.renatofreire.passwordmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final EncryptionService encryptionService;

    @Autowired
    public LoginService(UserRepository userRepository, LoginRepository loginRepository, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
        this.encryptionService = encryptionService;
    }

    public LoginOutDTO createNewPassword(final LoginInDTO newLogin, final UserDetails userDetails) {
        if(newLogin.name() == null || newLogin.password() == null || newLogin.username() == null){
            throw new InvalidFieldException("Name, password or username cannot be null");
        }

        final User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User was not found"));

        Optional<Login> login = loginRepository.findByNameAndUserEmail(newLogin.name(), userDetails.getUsername());
        if(login.isPresent()){
            throw new EntityAlreadyExistsException();
        }

        byte[] encodedPassword = encryptionService.encode(newLogin.password(), user);
        Login finalLogin = new Login(newLogin, user, encodedPassword);

        return LoginMapper.map(loginRepository.save(finalLogin), newLogin.password());
    }

    public LoginOutDTO updateNewPassword(final Long loginId, final LoginInDTO updatedLogin , final UserDetails userDetails) {
        if(updatedLogin.name() == null || updatedLogin.password() == null || updatedLogin.username() == null){
            throw new InvalidFieldException("Name, password or username cannot be null");
        }

        final User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User was not found"));

        Login outdatedLogin = loginRepository.findById(loginId)
                .orElseThrow(() -> new EntityNotFoundException("Password was not found"));

        if(!user.getEmail().equals(outdatedLogin.getUser().getEmail())){
            throw new AccessDeniedException("User denied");
        }

        byte[] encodedPassword = encryptionService.encode(updatedLogin.password(), user);
        outdatedLogin = LoginMapper.map(updatedLogin, outdatedLogin, encodedPassword);

        return LoginMapper.map(loginRepository.save(outdatedLogin), updatedLogin.password());
    }

    public boolean deletePassword(final Long passwordId, final UserDetails userDetails) {

        Login loginToDelete = loginRepository.findById(passwordId)
                .orElseThrow(() -> new EntityNotFoundException("Password was not found"));

        if(!userDetails.getUsername().equals(loginToDelete.getUser().getEmail())){
            throw new AccessDeniedException("User denied");
        }

        loginRepository.delete(loginToDelete);
        return true;
    }

    public List<LoginOutDTO> getAll(final UserDetails userDetails) {
        return
                loginRepository.findByUserEmail(userDetails.getUsername())
                .stream()
                .map(login -> {
                    String decodedPassword = encryptionService.decode(login.getPassword(), login.getUser());
                    return LoginMapper.map(login, decodedPassword);
                })
                .collect(Collectors.toList());
    }

    public LoginOutDTO getById(final UserDetails userDetails, final Long passwordId) {
        Login login = loginRepository.findById(passwordId)
                .orElseThrow(() -> new EntityNotFoundException("Password was not found"));

        if(!userDetails.getUsername().equals(login.getUser().getEmail())){
            throw new AccessDeniedException("User denied");
        }

        String decodedPassword = encryptionService.decode(login.getPassword(), login.getUser());
        return LoginMapper.map(login, decodedPassword);
    }

}
