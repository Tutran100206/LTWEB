package com.example.demo.dto;
import jakarta.validation.constraints.*;
public record RegisterUserRequest(
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(min = 6, max = 72) String password,
    @NotBlank @Size(max = 120) String fullName) {
    @Override public String toString() { return "RegisterUserRequest[credentials redacted]"; }
}
