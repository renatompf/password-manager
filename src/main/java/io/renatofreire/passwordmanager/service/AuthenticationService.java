package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.response.AuthenticationResponse;
import io.renatofreire.passwordmanager.dto.request.LoginDTO;
import io.renatofreire.passwordmanager.dto.request.RegisterDTO;
import io.renatofreire.passwordmanager.model.User;
import io.renatofreire.passwordmanager.repository.UserRepository;
import io.renatofreire.passwordmanager.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    public AuthenticationResponse register(RegisterDTO registerRequest) {
        User newUser = new User(registerRequest.firstName(),registerRequest.lastName(),
                registerRequest.email(), passwordEncoder.encode(registerRequest.password()));

        userRepository.save(newUser);

        return new AuthenticationResponse(jwtService.generateJWTToken(newUser));
    }

    public AuthenticationResponse authenticate(LoginDTO authenticateRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateRequest.email(),
                        authenticateRequest.password()
                )
        );

        User user = userRepository.findByEmail(authenticateRequest.email()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new AuthenticationResponse(jwtService.generateJWTToken(user));
    }
}
