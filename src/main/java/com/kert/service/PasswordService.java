package com.kert.service;

import com.kert.dto.PasswordDTO;
import com.kert.model.Password;
import com.kert.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordRepository passwordRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Password createPassword(PasswordDTO passwordDTO) {
        String hashedPassword = passwordEncoder.encode(passwordDTO.getPassword());

        Password password = new Password();
        password.setUserId(passwordDTO.getUserId());
        password.setHash(hashedPassword);

        return passwordRepository.save(password);
    }

    public Optional<Password> getPasswordByUserId(Long userId) {
        return passwordRepository.findByUserId(userId);
    }

    public Password updatePassword(Long userId, PasswordDTO passwordDTO) {
        Optional<Password> existingPasswordOptional = passwordRepository.findByUserId(userId);
        if (existingPasswordOptional.isPresent()) {
            Password existingPassword = existingPasswordOptional.get();
            if (passwordEncoder.matches(passwordDTO.getOldPassword(), existingPassword.getHash())) {
                String newHashedPassword = passwordEncoder.encode(passwordDTO.getPassword());
                existingPassword.setHash(newHashedPassword);
                return passwordRepository.save(existingPassword);
            } else {
                return null;
            }
        }
        return null;
    }

    public void deletePassword(Long userId) {
        passwordRepository.deleteByUserId(userId);
    }
}
