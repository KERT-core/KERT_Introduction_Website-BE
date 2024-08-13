package com.kert.controller;

import com.kert.model.Password;
import com.kert.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/passwords")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @PostMapping
    public ResponseEntity<Password> createPassword(@RequestBody Password password) {
        Password createdPassword = passwordService.createPassword(password.getUserId(), password.getHash());
        return ResponseEntity.ok(createdPassword);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<Password> getPassword(@PathVariable("user_id") Long userId) {
        Optional<Password> password = passwordService.getPasswordByUserId(userId);
        return password.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{user_id}")
    public ResponseEntity<Password> updatePassword(@PathVariable("user_id") Long userId, @RequestBody Password password) {
        Password updatedPassword = passwordService.updatePassword(userId, password.getHash());
        if (updatedPassword != null) {
            return ResponseEntity.ok(updatedPassword);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deletePassword(@PathVariable("user_id") Long userId) {
        passwordService.deletePassword(userId);
        return ResponseEntity.noContent().build();
    }
}
