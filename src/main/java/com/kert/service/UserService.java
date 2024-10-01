package com.kert.service;

import com.kert.model.User;
import com.kert.repository.UserRepository;
// import com.kert.dto.PasswordDTO;
import com.kert.dto.SignUpDTO;
import com.kert.model.Password;
import com.kert.repository.PasswordRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long studentId) {
        return userRepository.findById(studentId).orElse(null);
    }


    public User createUser(SignUpDTO signUpDTO) {
        User user = new User();

        user.setStudentId(signUpDTO.getStudentId());
        user.setEmail(signUpDTO.getEmail());
        user.setName(signUpDTO.getUsername());
        user.setProfilePicture(signUpDTO.getProfilePicture());
        user.setGeneration(signUpDTO.getGeneration());
        user.setMajor(signUpDTO.getMajor());

        System.out.println(user);

        if (userRepository.existsById(user.getStudentId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        Password password = new Password();
        password.setUserId(user.getStudentId());
        password.setHash(passwordEncoder.encode(signUpDTO.getPassword()));
        passwordRepository.save(password);

        return userRepository.save(user);
    }

    public boolean login(Long studentId, String rawPassword) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자이거나 비밀번호가 틀렸습니다."));
        Password password = passwordRepository.findByUserId(user.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자이거나 비밀번호가 틀렸습니다."));

        return passwordEncoder.matches(rawPassword, password.getHash());
    }

    public User updateUser(Long studentId, User userDetails) {
        User user = getUserById(studentId);

        if (user != null) {
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setProfilePicture(userDetails.getProfilePicture());
            user.setGeneration(userDetails.getGeneration());
            user.setMajor(userDetails.getMajor());

            return userRepository.save(user);
        }

        return null;
    }

    public void deleteUser(Long studentId) {
        userRepository.deleteById(studentId);
    }
}