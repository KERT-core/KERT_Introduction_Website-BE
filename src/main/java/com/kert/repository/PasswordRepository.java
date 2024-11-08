package com.kert.repository;

import com.kert.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
    Optional<Password> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
