package com.example.vidu3.controller;

import com.example.vidu3.security.CustomUserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@ControllerAdvice
public class CurrentUserAdvice {
    @ModelAttribute("currentUser") public CustomUserDetails currentUser(@AuthenticationPrincipal CustomUserDetails principal) { return principal; }
}
