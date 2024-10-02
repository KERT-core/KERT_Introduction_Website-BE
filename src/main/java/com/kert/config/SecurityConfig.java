package com.kert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kert.repository.AdminRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AdminRepository adminRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
            .requestMatchers("/", "/auth/**", "/oauth2/**", "/users/login", "/users/signup").permitAll()
            .requestMatchers(HttpMethod.POST, "/admin/**", "/posts/**", "/histories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/admin/**", "/posts/**", "/histories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/admin/**", "/posts/**", "/histories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,"/users/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.GET, "/posts", "/histories").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/")
                .defaultSuccessUrl("/users")
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/")
                .defaultSuccessUrl("/users", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .userAuthoritiesMapper(grantedAuthoritiesMapper())
                )
            )
            .sessionManagement(session -> session
                // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.disable())
            );
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority) {
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    if (isAdmin((OidcUserAuthority) authority)) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                } else if (authority instanceof OAuth2UserAuthority) {
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    if (isAdmin((OAuth2UserAuthority) authority)) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                } else {
                    mappedAuthorities.add(authority);
                }
            });

            return mappedAuthorities;
        };
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private boolean isAdmin(OidcUserAuthority oidcUserAuthority) {
        Long userId = Long.valueOf((String) oidcUserAuthority.getAttributes().get("sub"));
        return checkIfUserIsAdmin(userId);
    }
    
    private boolean isAdmin(OAuth2UserAuthority oauth2UserAuthority) {
        Long userId = Long.valueOf((String) oauth2UserAuthority.getAttributes().get("sub"));
        return checkIfUserIsAdmin(userId);
    }

    private boolean checkIfUserIsAdmin(Long userId) {
        return adminRepository.findById(userId).isPresent();
    }

}