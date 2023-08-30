package io.renatofreire.passwordmanager.controller;

import io.renatofreire.passwordmanager.dto.request.PasswordInDTO;
import io.renatofreire.passwordmanager.dto.response.PasswordOutDTO;
import io.renatofreire.passwordmanager.model.Password;
import io.renatofreire.passwordmanager.service.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/passwords")
public class PasswordController {

    private final PasswordService passwordService;


    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping
    public ResponseEntity<List<Password>> getAllPasswordsFromUser(@AuthenticationPrincipal UserDetails userDetails){
        List<Password> all = passwordService.getAll(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(all);
    }

    @GetMapping("/{passwordId}")
    public ResponseEntity<PasswordOutDTO> getById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable final Long passwordId){
        return ResponseEntity.status(HttpStatus.OK).body(passwordService.getById(userDetails, passwordId));
    }

    @PostMapping
    public ResponseEntity<PasswordOutDTO> createNewPassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody final PasswordInDTO newPassword){
        return ResponseEntity.status(HttpStatus.OK).body(passwordService.createNewPassword(newPassword, userDetails));
    }

    @PutMapping("/{passwordId}")
    public ResponseEntity<PasswordOutDTO> updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable final Long passwordId,
                                                         @RequestBody final PasswordInDTO updatedPassword){
        return ResponseEntity.status(HttpStatus.OK).body(passwordService.updateNewPassword(passwordId, updatedPassword, userDetails));
    }

    @DeleteMapping("/{passwordId}")
    public ResponseEntity<Boolean> deletePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable final Long passwordId){
        return ResponseEntity.status(HttpStatus.OK).body(passwordService.deletePassword(passwordId, userDetails));
    }

}
