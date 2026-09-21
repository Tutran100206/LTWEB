package com.example.vidu3.config;

import com.example.vidu3.entity.*;
import com.example.vidu3.repository.*;
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
    private final ProductRepository products;
    @Override @Transactional public void run(String... args) {
        Role admin=role("ADMIN"), user=role("USER");
        seed("admin", "Quản trị viên", admin); seed("user", "Nguyễn Minh An", user);
        if (products.count()==0) {
            User owner=users.findByEmail("user@vidu3.local").orElseThrow();
            Product p=new Product(); p.setName("Sổ tay học Spring"); p.setDescription("Ghi chép kiến thức mỗi ngày"); p.setPrice(new java.math.BigDecimal("59000")); p.setUser(owner); products.save(p);
        }
    }
    private Role role(String name) { return roles.findByName(name).orElseGet(() -> { Role r=new Role(); r.setName(name); return roles.save(r); }); }
    private void seed(String login, String name, Role role) {
        String email=login + "@vidu3.local";
        if (users.existsByEmail(email)) return;
        User u=new User(); u.setEmail(email); u.setFullName(name); u.setEnabled(true); u.setRole(role);
        u.setUsername(login);
        u.setPassword(encoder.encode("Demo@12345")); users.save(u);
    }
}
