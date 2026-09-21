package com.example.vidu2.security;

import com.example.vidu2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.*;
@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    @Override @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return users.findByUsernameOrEmail(login.strip().toLowerCase(java.util.Locale.ROOT), login.strip().toLowerCase(java.util.Locale.ROOT)).map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("Thông tin đăng nhập không hợp lệ"));
    }
}
