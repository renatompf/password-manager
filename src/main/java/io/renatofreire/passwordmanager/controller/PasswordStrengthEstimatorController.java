package io.renatofreire.passwordmanager.controller;

import com.nulabinc.zxcvbn.Strength;
import io.renatofreire.passwordmanager.dto.response.PasswordStrengthDTO;
import io.renatofreire.passwordmanager.service.PasswordStrengthEstimatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password-strength")
public class PasswordStrengthEstimatorController {

    private final PasswordStrengthEstimatorService passwordStrengthEstimatorService;

    @Autowired
    public PasswordStrengthEstimatorController(PasswordStrengthEstimatorService passwordStrengthEstimatorService) {
        this.passwordStrengthEstimatorService = passwordStrengthEstimatorService;
    }

    @PostMapping
    public ResponseEntity<PasswordStrengthDTO> passwordStrengthEstimator(@RequestBody String password){
        return ResponseEntity.status(HttpStatus.OK)
                .body(passwordStrengthEstimatorService.measurePasswordStrength(password));
    }
}
