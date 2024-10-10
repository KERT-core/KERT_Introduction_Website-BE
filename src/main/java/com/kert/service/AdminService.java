package com.kert.service;

import com.kert.model.Admin;
import com.kert.model.User;
import com.kert.repository.AdminRepository;
import com.kert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin getAdminByStudentId(Long studentId) {
        return adminRepository.findById(studentId).orElse(null);
    }

    public Admin createAdmin(Admin admin) {
        User user = userRepository.findById(admin.getStudentId()).orElse(null);

        if (user != null) {
            admin.setUser(user);
            return adminRepository.save(admin);
        }

        return null;
    }

    public Admin updateAdmin(Long studentId, Admin adminDetails) {
        Admin admin = getAdminByStudentId(studentId);

        if (admin != null) {
            admin.setGeneration(adminDetails.getGeneration());
            admin.setRole(adminDetails.getRole());
            admin.setDescription(adminDetails.getDescription());
            return adminRepository.save(admin);
        }

        return null;
    }

    public void deleteAdmin(Long studentId) {
        adminRepository.deleteById(studentId);
        userRepository.deleteById(studentId);
    }
}
