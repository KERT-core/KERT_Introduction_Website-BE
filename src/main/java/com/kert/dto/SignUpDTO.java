package com.kert.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignUpDTO {
    private Long studentId;
    private String username;
    private String password;
    private String email;
    private String profilePicture;
    private String generation;
    private String major;

}
