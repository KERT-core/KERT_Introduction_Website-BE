package com.kert.config;

import com.kert.model.Password;
import com.kert.model.Admin;
import com.kert.model.User;
import com.kert.repository.AdminRepository;
import com.kert.repository.PasswordRepository;
import com.kert.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordRepository passwordRepository;

    public SecurityUserService(UserRepository userRepository, AdminRepository adminRepository, PasswordRepository passwordRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordRepository = passwordRepository;
    }

    public SecurityUser loadUserById(Long studentId) throws UsernameNotFoundException {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        Password password = passwordRepository.findByUserId(user.getStudentId())
                .orElseThrow(() -> new UsernameNotFoundException("Unthorized"));

        Admin admin = adminRepository.findById(studentId).orElse(null);

        return new SecurityUser(user, admin, password);
    }
    
    public boolean isAdminById(Long studentId) {
        Admin admin = adminRepository.findById(studentId).orElse(null);
        return admin != null;
    }
}