package com.example.demo.controller;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService users;
    public UserController(UserService users) { this.users = users; }
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) { return UserResponse.from(user); }
    @GetMapping({"", "/"})
    public List<UserResponse> all() { return users.allUsers().stream().map(UserResponse::from).toList(); }
}
