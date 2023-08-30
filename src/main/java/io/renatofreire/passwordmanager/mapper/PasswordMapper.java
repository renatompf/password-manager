package io.renatofreire.passwordmanager.mapper;

import io.renatofreire.passwordmanager.model.Password;
import io.renatofreire.passwordmanager.model.User;
import io.renatofreire.passwordmanager.dto.request.PasswordInDTO;
import io.renatofreire.passwordmanager.dto.response.PasswordOutDTO;

public class PasswordMapper {
    public static PasswordOutDTO map(Password password){
        return new PasswordOutDTO(password.getName(), password.getUsername(), password.getPassword(), password.getUrl(), password.getDescription());
    }

    public static Password map(PasswordInDTO password, User user){
        return new Password(password.name(), password.username(),
                password.password(), password.url(), password.description(), user);
    }

    public static Password map(PasswordInDTO password, Password outdated){
        return new Password(outdated.getId(), password.name(), password.username(),
                password.password(), password.url(), password.description(), outdated.getUser());
    }

}
