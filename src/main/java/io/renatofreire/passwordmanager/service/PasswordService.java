package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.request.PasswordInDTO;
import io.renatofreire.passwordmanager.dto.response.PasswordOutDTO;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import io.renatofreire.passwordmanager.mapper.PasswordMapper;
import io.renatofreire.passwordmanager.model.Password;
import io.renatofreire.passwordmanager.model.User;
import io.renatofreire.passwordmanager.repository.PasswordRepository;
import io.renatofreire.passwordmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;

    @Autowired
    public PasswordService(UserRepository userRepository, PasswordRepository passwordRepository) {
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
    }

    public PasswordOutDTO createNewPassword(final PasswordInDTO newPassword, final UserDetails userDetails) {
        if(newPassword.name() == null || newPassword.password() == null || newPassword.username() == null){
            throw new InvalidFieldException("Name, password or username cannot be null");
        }

        final User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User was not found"));

        Password finalPassword = PasswordMapper.map(newPassword, user);

        return PasswordMapper.map(passwordRepository.save(finalPassword));
    }

    public PasswordOutDTO updateNewPassword(final Long passwordId, final PasswordInDTO updatedPassword , final UserDetails userDetails) {
        if(updatedPassword.name() == null || updatedPassword.password() == null || updatedPassword.username() == null){
            throw new InvalidFieldException("Name, password or username cannot be null");
        }

        final User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User was not found"));

        Password outdatedPassword = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new EntityNotFoundException("Password was not found"));

        if(!user.getEmail().equals(outdatedPassword.getUser().getEmail())){
            throw new AccessDeniedException("User denied");
        }

        outdatedPassword = PasswordMapper.map(updatedPassword, outdatedPassword);

        return PasswordMapper.map(passwordRepository.save(outdatedPassword));
    }

    public boolean deletePassword(final Long passwordId, final UserDetails userDetails) {

        Password passwordToDelete = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new EntityNotFoundException("Password was not found"));

        if(!userDetails.getUsername().equals(passwordToDelete.getUser().getEmail())){
            throw new AccessDeniedException("User denied");
        }

        passwordRepository.delete(passwordToDelete);
        return true;
    }

    public List<Password> getAll(final UserDetails userDetails) {
        return passwordRepository.findByUserEmail(userDetails.getUsername());
    }

    public PasswordOutDTO getById(final UserDetails userDetails, final Long passwordId) {
        Password password  = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new EntityNotFoundException("Password was not found"));

        if(!userDetails.getUsername().equals(password.getUser().getEmail())){
            throw new AccessDeniedException("User denied");
        }

        return PasswordMapper.map(password);
    }

}
