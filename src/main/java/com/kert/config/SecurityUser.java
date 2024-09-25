package com.kert.config;

import com.kert.model.Admin;
import com.kert.model.Password;
import com.kert.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.*;

public class SecurityUser implements UserDetails {
    private final User user;
    private final Admin admin;
    private final Password password;

    public SecurityUser(User user, Admin admin, Password password) {
        this.user = user;
        this.admin = admin;
        this.password = password;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (admin != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
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

