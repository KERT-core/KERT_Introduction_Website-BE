package com.kert.controller;

import com.kert.config.JwtTokenProvider;
import com.kert.dto.RefreshTokenRequest;
import com.kert.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    // Refresh Token으로 새로운 Access Token 및 Refresh Token 발급
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        TokenResponse tokenResponse = jwtTokenProvider.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(tokenResponse);
    }
}
