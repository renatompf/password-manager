package io.renatofreire.passwordmanager.controller;

import io.renatofreire.passwordmanager.dto.request.LoginInDTO;
import io.renatofreire.passwordmanager.dto.response.LoginOutDTO;
import io.renatofreire.passwordmanager.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/logins")
public class LoginController {

    private final LoginService loginService;


    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping
    public ResponseEntity<List<LoginOutDTO>> getAllLoginsFromUser(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(HttpStatus.OK).body(loginService.getAll(userDetails));
    }

    @GetMapping("/{loginId}")
    public ResponseEntity<LoginOutDTO> getById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable final Long loginId){
        return ResponseEntity.status(HttpStatus.OK).body(loginService.getById(userDetails, loginId));
    }

    @PostMapping
    public ResponseEntity<LoginOutDTO> createNewLogin(@AuthenticationPrincipal UserDetails userDetails, @RequestBody final LoginInDTO newLogin){
        return ResponseEntity.status(HttpStatus.OK).body(loginService.createNewLogin(newLogin, userDetails));
    }

    @PutMapping("/{loginId}")
    public ResponseEntity<LoginOutDTO> updateLogin(@AuthenticationPrincipal UserDetails userDetails,
                                                   @PathVariable final Long loginId,
                                                   @RequestBody final LoginInDTO updatedLogin){
        return ResponseEntity.status(HttpStatus.OK).body(loginService.updateLogin(loginId, updatedLogin, userDetails));
    }

    @DeleteMapping("/{loginId}")
    public ResponseEntity<Boolean> deleteLogin(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable final Long loginId){
        return ResponseEntity.status(HttpStatus.OK).body(loginService.deleteLogin(loginId, userDetails));
    }

}
