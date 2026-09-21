package com.example.vidu1.config;

import com.example.vidu1.entity.*;
import com.example.vidu1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component @RequiredArgsConstructor @ConditionalOnProperty(name="app.seed-demo", havingValue="true", matchIfMissing=true)
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roles;
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Override @Transactional public void run(String... args) {
        Role admin=role("ADMIN"), user=role("USER");
        seed("admin", "Quản trị viên", admin); seed("user", "Nguyễn Minh An", user);

    }
    private Role role(String name) { return roles.findByName(name).orElseGet(() -> { Role r=new Role(); r.setName(name); return roles.save(r); }); }
    private void seed(String login, String name, Role role) {
        String email=login + "@vidu1.local";
        if (users.existsByEmail(email)) return;
        User u=new User(); u.setEmail(email); u.setFullName(name); u.setEnabled(true); u.setRole(role);

        u.setPassword(encoder.encode("Demo@12345")); users.save(u);
    }
}
