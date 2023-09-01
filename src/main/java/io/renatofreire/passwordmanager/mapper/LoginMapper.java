package io.renatofreire.passwordmanager.mapper;

import io.renatofreire.passwordmanager.model.Login;
import io.renatofreire.passwordmanager.model.User;
import io.renatofreire.passwordmanager.dto.request.LoginInDTO;
import io.renatofreire.passwordmanager.dto.response.LoginOutDTO;

public class LoginMapper {
    public static LoginOutDTO map(Login login, String decodedPassword){
        return new LoginOutDTO(login.getId(),
                login.getName(),
                login.getUsername(),
                decodedPassword,
                login.getUrl(),
                login.getDescription(),
                login.getUser().getId());
    }

    public static Login map(LoginInDTO password, Login outdated, byte[] encodedPassword){
        return new Login(outdated.getId(), password.name(), password.username(),
                encodedPassword, password.url(), password.description(), outdated.getUser());
    }

}
