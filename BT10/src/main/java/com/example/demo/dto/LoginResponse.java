package com.example.demo.dto;
public record LoginResponse(String token, long expiresIn) {
    @Override public String toString() { return "LoginResponse[token redacted, expiresIn=" + expiresIn + "]"; }
}
