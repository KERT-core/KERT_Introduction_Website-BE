package com.kert.config;

import com.kert.model.Password;
import com.kert.model.Admin;
import com.kert.model.User;
import com.kert.repository.AdminRepository;
import com.kert.repository.PasswordRepository;
import com.kert.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Service
public class SecurityUserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordRepository passwordRepository;

    public SecurityUserService(UserRepository userRepository, AdminRepository adminRepository, PasswordRepository passwordRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordRepository = passwordRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Long userId = Long.valueOf(username);
            return loadUserById(userId);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid User ID format");
        }
    }

    public SecurityUser loadUserById(Long studentId) throws UsernameNotFoundException {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        Password password = passwordRepository.findByUserId(user.getStudentId())
                .orElseThrow(() -> new UsernameNotFoundException("Unauthorized"));

        Admin admin = adminRepository.findById(studentId).orElse(null);

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (admin != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new SecurityUser(user, admin, password, authorities);
    }

    public boolean isAdminById(Long studentId) {
        Admin admin = adminRepository.findById(studentId).orElse(null);
        return admin != null;
    }
}
