package com.example.demo.service;
import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;
    public AuthenticationService(UserRepository repository, PasswordEncoder encoder, AuthenticationManager manager) {
        this.repository = repository; this.encoder = encoder; this.manager = manager;
    }
    private void checkPasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72)
            throw new IllegalArgumentException("Password must not exceed 72 UTF-8 bytes");
    }
    public User signup(RegisterUserRequest request) {
        checkPasswordLength(request.password());
        String email = UserService.normalizeEmail(request.email());
        if (repository.existsByEmail(email)) throw new EmailAlreadyExistsException();
        try {
            return repository.saveAndFlush(new User(request.fullName().strip(), email, encoder.encode(request.password())));
        } catch (DataIntegrityViolationException ex) {
            if (repository.existsByEmail(email)) throw new EmailAlreadyExistsException();
            throw ex;
        }
    }
    public User authenticate(LoginRequest request) {
        checkPasswordLength(request.password());
        return (User) manager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
            UserService.normalizeEmail(request.email()), request.password())).getPrincipal();
    }
}
