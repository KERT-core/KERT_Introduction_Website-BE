package com.kert.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {
    private Long userId;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 최소 8자 이상, 하나 이상의 대문자, 소문자, 숫자 및 특수문자가 포함되어야 합니다.")
    @Pattern(regexp = "^[^'\";#-]*$", message = "비밀번호에 SQL Injection에 사용될 수 있는 문자를 포함할 수 없습니다.")
    private String password;

    @NotBlank(message = "이전 비밀번호는 필수 항목입니다.")
    @Pattern(regexp = "^[^'\";#-]*$", message = "이전 비밀번호에 SQL Injection에 사용될 수 있는 문자를 포함할 수 없습니다.")
    private String oldPassword;
}
