package com.kert.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;
}