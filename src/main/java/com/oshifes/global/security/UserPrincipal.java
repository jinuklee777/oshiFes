package com.oshifes.global.security;

import com.oshifes.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPrincipal implements OAuth2User, UserDetails {

    private final Long userId;
    private final String role;
    private final Map<String, Object> attributes;

    public static UserPrincipal of(User user, Map<String, Object> attributes) {
        return new UserPrincipal(user.getId(), user.getRole().name(), attributes);
    }

    public static UserPrincipal of(Long userId, String role, Map<String, Object> attributes) {
        return new UserPrincipal(userId, role, attributes);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }
}
