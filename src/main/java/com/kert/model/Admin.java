package com.kert.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "studentId")
    private User user;

    private String generation;
    private String role;
}
