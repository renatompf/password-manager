package io.renatofreire.passwordmanager.mapper;

import io.renatofreire.passwordmanager.model.Login;
import io.renatofreire.passwordmanager.model.User;
import io.renatofreire.passwordmanager.dto.request.LoginInDTO;
import io.renatofreire.passwordmanager.dto.response.LoginOutDTO;

public class LoginMapper {
    public static LoginOutDTO map(Login login){
        return new LoginOutDTO(login.getId(),
                login.getName(),
                login.getUsername(),
                login.getPassword(),
                login.getUrl(),
                login.getDescription(),
                login.getUser().getId());
    }

    public static Login map(LoginInDTO password, User user){
        return new Login(password.name(), password.username(),
                password.password(), password.url(), password.description(), user);
    }

    public static Login map(LoginInDTO password, Login outdated){
        return new Login(outdated.getId(), password.name(), password.username(),
                password.password(), password.url(), password.description(), outdated.getUser());
    }

}
