package io.renatofreire.passwordmanager.controller;

import io.renatofreire.passwordmanager.exception.BodyIsMissingException;
import io.renatofreire.passwordmanager.dto.request.GeneratePasswordRequestDTO;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import io.renatofreire.passwordmanager.service.PasswordGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password-generator")
public class PasswordGeneratorController {

    private final PasswordGeneratorService passwordGeneratorService;

    @Autowired
    public PasswordGeneratorController(PasswordGeneratorService passwordGeneratorService) {
        this.passwordGeneratorService = passwordGeneratorService;
    }

    @PostMapping
    public ResponseEntity<String> generatePassword(@RequestBody final GeneratePasswordRequestDTO passwordFields){
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(passwordGeneratorService.generatePassword(passwordFields));
        } catch (BodyIsMissingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InvalidFieldException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
