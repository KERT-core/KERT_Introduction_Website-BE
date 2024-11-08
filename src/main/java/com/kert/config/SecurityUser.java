package com.kert.config;

import com.kert.model.Admin;
import com.kert.model.Password;
import com.kert.model.User;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;

@RequiredArgsConstructor
public class SecurityUser implements UserDetails {
    private final User user;
    private final Admin admin;
    private final Password password;
    private final Set<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password.getHash();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    public Long getUserId() {
        return user.getStudentId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
