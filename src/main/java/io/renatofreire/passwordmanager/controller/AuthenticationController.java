package io.renatofreire.passwordmanager.controller;

import io.renatofreire.passwordmanager.dto.response.AuthenticationResponse;
import io.renatofreire.passwordmanager.dto.request.AuthenticationDTO;
import io.renatofreire.passwordmanager.dto.request.RegisterDTO;
import io.renatofreire.passwordmanager.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterDTO registerRequest){
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationDTO authenticateRequest){
        return ResponseEntity.ok(authenticationService.authenticate(authenticateRequest));
    }


}
