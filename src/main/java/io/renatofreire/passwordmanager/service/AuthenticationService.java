package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.response.AuthenticationResponse;
import io.renatofreire.passwordmanager.dto.request.AuthenticationDTO;
import io.renatofreire.passwordmanager.dto.request.RegisterDTO;
import io.renatofreire.passwordmanager.enums.TokenType;
import io.renatofreire.passwordmanager.model.Token;
import io.renatofreire.passwordmanager.model.User;
import io.renatofreire.passwordmanager.repository.TokenRepository;
import io.renatofreire.passwordmanager.repository.UserRepository;
import io.renatofreire.passwordmanager.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SecurityService securityService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, SecurityService securityService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.securityService = securityService;
    }


    public AuthenticationResponse register(RegisterDTO registerRequest) {

        HashMap<String, byte[]> generatedKeys = securityService.generateKeyPairValue();

        final User newUser = new User(registerRequest.firstName(),registerRequest.lastName(),
                registerRequest.email(), passwordEncoder.encode(registerRequest.password()),
                generatedKeys.get("public"), generatedKeys.get("private"));

        final String jwtToken = saveJWTToken(newUser);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationDTO authenticateRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateRequest.email(),
                        authenticateRequest.password()
                )
        );

        final User user = userRepository.findByEmail(authenticateRequest.email()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        revokeAllUserTokens(user);
        final String jwtToken = saveJWTToken(user);

        return new AuthenticationResponse(jwtToken);
    }

    private String saveJWTToken(User newUser) {
        final User savedUser = userRepository.save(newUser);

        final String jwtToken = jwtService.generateJWTToken(newUser);

        Token token = new Token(jwtToken, TokenType.BEARER, false,false, savedUser);
        tokenRepository.save(token);
        return jwtToken;
    }

    private void revokeAllUserTokens(User newUser) {
        List<Token> allValidTokens = tokenRepository.findAllValidTokensByUser(newUser.getId());
        if(allValidTokens.isEmpty()){
            return;
        }

        allValidTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoke(true);
        });

        tokenRepository.saveAll(allValidTokens);
    }

}
