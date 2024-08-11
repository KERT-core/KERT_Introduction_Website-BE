package com.kert.service;

import com.kert.model.User;
import com.kert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long studentId) {
        return userRepository.findById(studentId).orElse(null);
    }

    public User createUser(User user) {
        if (userRepository.existsById(user.getStudentId())) {
            throw new IllegalArgumentException("Student with id " + user.getStudentId() + " already exists");
        }
        return userRepository.save(user);
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
