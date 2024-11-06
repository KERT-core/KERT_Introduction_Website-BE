package com.kert.controller;

import com.kert.model.User;
import com.kert.service.AdminService;
import com.kert.service.UserService;
import com.kert.dto.SignUpDTO;
import com.kert.dto.LoginDTO;
import com.kert.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kert.config.JwtTokenProvider;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdminService adminService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            Long currentUserId = jwtTokenProvider.getUserIdFromJWT(token);
            boolean isAdmin = adminService.getAdminByStudentId(currentUserId) != null;
            if (isAdmin) {
                List<User> users = userService.getAllUsers();
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<User> getUserById(@PathVariable("studentId") Long studentId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long currentUserId = jwtTokenProvider.getUserIdFromJWT(token);

        User user = userService.getUserById(studentId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = adminService.getAdminByStudentId(currentUserId) != null;
        if (isAdmin || currentUserId.equals(studentId)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginDTO loginDTO) {
        boolean success = userService.login(loginDTO.getStudentId(), loginDTO.getPassword());
        if (success) {
            String accessToken = jwtTokenProvider.generateToken(loginDTO.getStudentId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(loginDTO.getStudentId());
            return ResponseEntity.ok(new TokenResponse("Bearer " + accessToken, refreshToken));
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<User> updateUser(@PathVariable Long studentId, @RequestHeader("Authorization") String authHeader, @RequestBody User userDetails) {
        String token = authHeader.replace("Bearer ", "");

        Long currentUserId = jwtTokenProvider.getUserIdFromJWT(token);
        boolean isAdmin = adminService.getAdminByStudentId(currentUserId) != null;
        if (isAdmin || currentUserId.equals(studentId)) {
            User updatedUser = userService.updateUser(studentId, userDetails);
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long studentId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        User user = userService.getUserById(studentId);
        Long currentUserId = jwtTokenProvider.getUserIdFromJWT(token);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        boolean isAdmin = adminService.getAdminByStudentId(currentUserId) != null;
        if (isAdmin || currentUserId.equals(studentId)) {
            userService.deleteUser(studentId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}