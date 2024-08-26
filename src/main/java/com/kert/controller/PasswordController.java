package com.kert.controller;

import com.kert.dto.PasswordDTO;
import com.kert.model.Password;
import com.kert.service.PasswordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/passwords")
public class PasswordController {
    private final PasswordService passwordService;

    @PostMapping
    public ResponseEntity<Password> createPassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        Password createdPassword = passwordService.createPassword(passwordDTO);
        return ResponseEntity.ok(createdPassword);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<Password> getPassword(@PathVariable("user_id") Long userId) {
        Optional<Password> password = passwordService.getPasswordByUserId(userId);
        return password.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<Password> updatePassword(@PathVariable("user_id") Long userId, @RequestBody PasswordDTO passwordDTO) {
        Password updatedPassword = passwordService.updatePassword(userId, passwordDTO);
        if (updatedPassword != null) {
            return ResponseEntity.ok(updatedPassword);
        }
        return ResponseEntity.notFound().build();
    }

    @Transactional
    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deletePassword(@PathVariable("user_id") Long userId) {
        passwordService.deletePassword(userId);
        return ResponseEntity.noContent().build();
    }
}
