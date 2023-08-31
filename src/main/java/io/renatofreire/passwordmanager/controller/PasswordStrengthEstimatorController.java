package io.renatofreire.passwordmanager.controller;

import io.renatofreire.passwordmanager.dto.request.StrengthPasswordInDTO;
import io.renatofreire.passwordmanager.dto.response.PasswordStrengthDTO;
import io.renatofreire.passwordmanager.service.PasswordStrengthEstimatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password-strength")
public class PasswordStrengthEstimatorController {

    private final PasswordStrengthEstimatorService passwordStrengthEstimatorService;

    @Autowired
    public PasswordStrengthEstimatorController(PasswordStrengthEstimatorService passwordStrengthEstimatorService) {
        this.passwordStrengthEstimatorService = passwordStrengthEstimatorService;
    }

    @PostMapping
    public ResponseEntity<PasswordStrengthDTO> passwordStrengthEstimator(@RequestBody StrengthPasswordInDTO password){
        return ResponseEntity.status(HttpStatus.OK)
                .body(passwordStrengthEstimatorService.measurePasswordStrength(password));
    }
}
