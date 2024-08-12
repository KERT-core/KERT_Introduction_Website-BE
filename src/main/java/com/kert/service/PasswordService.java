package com.kert.service;

import com.kert.model.Password;
import com.kert.repository.PasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordService {

    @Autowired
    private PasswordRepository passwordRepository;

    public Password createPassword(Long userId, String hash) {
        Password password = new Password();
        password.setUserId(userId);
        password.setHash(hash);
        return passwordRepository.save(password);
    }

    public Optional<Password> getPasswordByUserId(Long userId) {
        return passwordRepository.findByUserId(userId);
    }

    public Password updatePassword(Long userId, String newHash) {
        Optional<Password> existingPassword = passwordRepository.findByUserId(userId);
        if (existingPassword.isPresent()) {
            Password password = existingPassword.get();
            password.setHash(newHash);
            return passwordRepository.save(password);
        }
        return null;
    }

    public void deletePassword(Long userId) {
        passwordRepository.deleteByUserId(userId);
    }
}
