package com.kert.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import com.kert.model.RefreshToken;
import com.kert.repository.AdminRepository;
import com.kert.repository.RefreshTokenRepository;
import com.kert.dto.TokenResponse;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import java.time.LocalDateTime;


@Configuration
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Dotenv dotenv = Dotenv.load();
    private final AdminRepository adminRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final String SECRET_KEY = dotenv.get("JWT_SECRET");
    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    // JWT Access Token 생성
    public String generateToken(Long studentId) {
        Date now = new Date();
        long EXPIRATION_TIME = 900000; //15분
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        Claims claims = Jwts.claims().setSubject(Long.toString(studentId));

        // 사용자 권한 설정
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (isAdmin(studentId)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        claims.put("roles", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Long studentId) {

        LocalDateTime now = LocalDateTime.now();
        long REFRESH_EXPIRATION_TIME = 1209600000L; // 2주

        // 기존 리프레시 토큰이 있으면 삭제
        if(refreshTokenRepository.findById(studentId).isPresent()) {
            refreshTokenRepository.deleteById(studentId);
        }

        // 새로운 리프레시 토큰의 만료 시간
        LocalDateTime expiryDate = now.plusSeconds(REFRESH_EXPIRATION_TIME / 1000);

        // JWT 리프레시 토큰 생성
        Date nowDate = new Date();
        Date expiryDateInDateFormat = new Date(nowDate.getTime() + REFRESH_EXPIRATION_TIME);
        String refreshToken = Jwts.builder()
                .setSubject(Long.toString(studentId))
                .setIssuedAt(nowDate)
                .setExpiration(expiryDateInDateFormat)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // 리프레시 토큰 엔티티 생성 후 저장
        RefreshToken tokenEntity = new RefreshToken(
                studentId,
                refreshToken,
                expiryDate,
                now,
                now
        );
        refreshTokenRepository.save(tokenEntity);

        return refreshToken;
    }
    // Refresh Token 검증 후 새로운 Access Token 발급
    public TokenResponse refreshAccessToken(String refreshToken) {
        Optional<RefreshToken> storedTokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (storedTokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        RefreshToken storedToken = storedTokenOpt.get();
        if (storedToken.isExpired()) {
            throw new IllegalArgumentException("Refresh token is expired");
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        Long studentId = Long.parseLong(claims.getSubject());

        // 새로운 Access Token 및 Refresh Token 발급
        String newAccessToken = generateToken(studentId);
        String newRefreshToken = generateRefreshToken(studentId);

        return new TokenResponse("Bearer " + newAccessToken, newRefreshToken);
    }

    // JWT로부터 사용자 ID 추출
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // JWT로부터 역할(Authorities) 추출
    public Set<GrantedAuthority> getAuthoritiesFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<?> roles = claims.get("roles", List.class);
        return roles.stream()
                .filter(role -> role instanceof String)
                .map(role -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toSet());
    }

    // JWT 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Admin 여부 확인
    private boolean isAdmin(Long studentId) {
        return adminRepository.findById(studentId).isPresent();
    }
}
