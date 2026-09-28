package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService auth;
    private final JwtService jwt;
    public AuthenticationController(AuthenticationService auth, JwtService jwt) { this.auth = auth; this.jwt = jwt; }
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(auth.signup(request)));
    }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return new LoginResponse(jwt.generateToken(auth.authenticate(request)), jwt.getExpirationTime());
    }
}
