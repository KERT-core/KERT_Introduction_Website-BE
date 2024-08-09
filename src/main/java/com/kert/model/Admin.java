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
    private Long studentId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "student_id")
    private User user;

    private String generation;
    private String role;
}
