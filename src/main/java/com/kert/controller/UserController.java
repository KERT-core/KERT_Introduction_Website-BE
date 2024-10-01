package com.kert.controller;

import com.kert.model.User;
import com.kert.service.UserService;
import com.kert.dto.SignUpDTO;
import com.kert.dto.LoginDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kert.config.JwtTokenProvider;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

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

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody SignUpDTO signUpDTO) {
        try {
            User createdUser = userService.createUser(signUpDTO);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO loginDTO) {
        boolean success = userService.login(loginDTO.getStudentId(), loginDTO.getPassword());
        if (success) {
            String token = jwtTokenProvider.generateToken(loginDTO.getStudentId());
            return ResponseEntity.ok("Bearer " + token);
        } else {
            return ResponseEntity.status(401).body("Login failed");
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
