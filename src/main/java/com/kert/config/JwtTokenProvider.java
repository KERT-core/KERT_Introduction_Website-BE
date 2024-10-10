package com.kert.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import com.kert.repository.AdminRepository;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;

@Configuration
public class JwtTokenProvider {

    private final Dotenv dotenv = Dotenv.load(); 

    private final String SECRET_KEY = dotenv.get("JWT_SECRET");
    private final long EXPIRATION_TIME = 86400000;
    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    @Autowired
    private AdminRepository adminRepository;

    // JWT 토큰 생성
    public String generateToken(Long studentId) {
        Date now = new Date();
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
        } catch (SignatureException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Admin 여부 확인
    private boolean isAdmin(Long studentId) {
        return adminRepository.findById(studentId).isPresent();
    }
}
