package com.example.demo.dto;
public record ApiResponse<T>(boolean status, String message, T body) {
    public static <T> ApiResponse<T> ok(String message, T body) {
        return new ApiResponse<>(true, message, body);
    }
}
