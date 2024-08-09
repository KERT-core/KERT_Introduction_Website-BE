package com.kert.controller;

import com.kert.model.Admin;
import com.kert.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long studentId) {
        Admin admin = adminService.getAdminByStudentId(studentId);

        if (admin == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(admin);
    }

    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin createdAdmin = adminService.createAdmin(admin);

        if (createdAdmin == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(createdAdmin);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long studentId, @RequestBody Admin adminDetails) {
        Admin updatedAdmin = adminService.updateAdmin(studentId, adminDetails);

        if (updatedAdmin == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedAdmin);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long studentId) {
        adminService.deleteAdmin(studentId);

        return ResponseEntity.noContent().build();
    }
}
