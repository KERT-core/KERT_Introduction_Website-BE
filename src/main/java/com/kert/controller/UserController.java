package com.kert.controller;

import com.kert.model.User;
import com.kert.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<User> getUserById(@PathVariable Long studentId) {
        User user = userService.getUserById(studentId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);

            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<User> updateUser(@PathVariable Long studentId, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(studentId, userDetails);

        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long studentId) {
        userService.deleteUser(studentId);

        return ResponseEntity.noContent().build();
    }
}
