package com.example.demo.service;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    public UserService(UserRepository repository) { this.repository = repository; }
    public static String normalizeEmail(String email) { return email.strip().toLowerCase(Locale.ROOT); }
    @Override public User loadUserByUsername(String email) {
        return repository.findByEmail(normalizeEmail(email))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public List<User> allUsers() { return repository.findAll(); }
}
