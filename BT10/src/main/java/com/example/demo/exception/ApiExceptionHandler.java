package com.example.demo.exception;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    private ResponseEntity<ApiError> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.value(), status.getReasonPhrase(), message));
    }
    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiError> unauthorized(AuthenticationException ex) { return error(HttpStatus.UNAUTHORIZED, "Invalid email or password"); }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> forbidden(AccessDeniedException ex) { return error(HttpStatus.FORBIDDEN, "Access denied"); }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    ResponseEntity<ApiError> conflict(EmailAlreadyExistsException ex) { return error(HttpStatus.CONFLICT, ex.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        var field = ex.getBindingResult().getFieldErrors().getFirst();
        return error(HttpStatus.BAD_REQUEST, field.getField() + ": " + field.getDefaultMessage());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> malformed(HttpMessageNotReadableException ex) { return error(HttpStatus.BAD_REQUEST, "Invalid JSON request"); }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> invalid(IllegalArgumentException ex) { return error(HttpStatus.BAD_REQUEST, ex.getMessage()); }
}
