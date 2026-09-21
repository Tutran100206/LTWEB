package com.example.vidu2.security;

import com.example.vidu2.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
@Getter
public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String email, password, fullName, role;
    private final boolean enabled;
    private final String username;
private final String images;

    public CustomUserDetails(User user) {
        id=user.getId(); email=user.getEmail(); password=user.getPassword();
        fullName=user.getFullName(); role=user.getRole().getName(); enabled=user.isEnabled();
        this.username=user.getUsername();
this.images=user.getImages();

    }
    @Override public String getUsername() { return username; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority("ROLE_" + role)); }
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
