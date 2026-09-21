package com.example.vidu1.controller;

import com.example.vidu1.security.CustomUserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@ControllerAdvice
public class CurrentUserAdvice {
    @ModelAttribute("currentUser") public CustomUserDetails currentUser(@AuthenticationPrincipal CustomUserDetails principal) { return principal; }
}
