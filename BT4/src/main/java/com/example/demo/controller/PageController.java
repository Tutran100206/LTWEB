package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class PageController {
    @GetMapping("/") public String home() { return "redirect:/admin/categories"; }
    @GetMapping("/admin/categories") public String categories() { return "categories"; }
    @GetMapping("/admin/products") public String products() { return "products"; }
}
