package com.example.vidu1.controller;

import com.example.vidu1.security.CustomUserDetails;
import com.example.vidu1.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
@Controller @RequiredArgsConstructor
public class PageController {
    private final UserProfileService profiles;
    @GetMapping("/") public String home() { return "home"; }
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/dashboard") public String dashboard(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("profile", profiles.find(principal.getId())); return "dashboard";
    }
    @GetMapping("/dashboard/admin") public String admin() { return "admin"; }
    @GetMapping("/access-denied") public String denied(jakarta.servlet.http.HttpServletResponse response) { response.setStatus(403); return "access-denied"; }
}
