package com.kert.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignUpDTO {
    private Long studentId;

    @NotBlank(message = "이름은 필수 항목입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "이름에는 특수문자를 포함할 수 없습니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 최소 8자 이상, 하나 이상의 대문자, 소문자, 숫자 및 특수문자가 포함되어야 합니다.")
    @Pattern(regexp = "^[^'\";#-]*$", message = "사용할 수 없는 특수문자 입니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @Pattern(regexp = "^[^'\";#-]*$", message = "사용할 수 없는 특수문자 입니다.")
    private String email;

    private String profilePicture;

    @NotBlank(message = "기수는 필수 항목입니다.")
    private String generation;

    @NotBlank(message = "전공은 필수 항목입니다.")
    private String major;
}
