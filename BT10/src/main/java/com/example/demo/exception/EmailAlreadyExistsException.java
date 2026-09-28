package com.example.demo.exception;
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() { super("Email already exists"); }
}
