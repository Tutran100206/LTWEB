package com.example.demo.exception;
public record ApiError(int status, String error, String message) {}
