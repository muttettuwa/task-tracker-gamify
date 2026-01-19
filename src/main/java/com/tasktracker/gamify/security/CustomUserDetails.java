package com.tasktracker.gamify.security;

import com.tasktracker.gamify.entity.UserInfo;
import com.tasktracker.gamify.entity.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom UserDetails implementation wrapping UserInfo entity
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public CustomUserDetails(UserInfo userInfo, List<UserRole> userRoles) {
        this.id = userInfo.getId();
        this.email = userInfo.getEmail();
        this.password = userInfo.getPasswordHash();
        this.enabled = userInfo.getStatus().name().equals("APPROVED") && !userInfo.getLogicallyDeleted();

        // Convert roles to authorities with ROLE_ prefix
        this.authorities = userRoles.stream()
                .filter(UserRole::getActive)
                .filter(ur -> !ur.getLogicallyDeleted())
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getSystemRole().getCode().name()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
