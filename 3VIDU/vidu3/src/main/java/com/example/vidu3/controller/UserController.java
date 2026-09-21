package com.example.vidu3.controller;

import com.example.vidu3.dto.UserFormDTO;
import com.example.vidu3.security.CustomUserDetails;
import com.example.vidu3.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequiredArgsConstructor @RequestMapping("/users")
public class UserController {
    private final UserService users;
    @GetMapping public String list(@RequestParam(defaultValue="") String keyword,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,Model model) {
        model.addAttribute("items",users.search(keyword,page,size)); model.addAttribute("totalUsers",users.count()); model.addAttribute("keyword",keyword); model.addAttribute("baseUrl","/users"); return "users/list";
    }
    @GetMapping("/create") public String create(Model model) { model.addAttribute("form",new UserFormDTO()); return "users/form"; }
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model model) { model.addAttribute("form",users.get(id)); return "users/form"; }
    @PostMapping public String create(@Valid @ModelAttribute("form") UserFormDTO form,BindingResult errors,@AuthenticationPrincipal CustomUserDetails actor,RedirectAttributes redirect) {
        form.setId(null);
        if (form.getPassword()==null || form.getPassword().isBlank()) errors.rejectValue("password","required","Vui lòng nhập mật khẩu.");
        return save(null,form,errors,actor,redirect);
    }
    @PostMapping("/{id}") public String update(@PathVariable Long id,@Valid @ModelAttribute("form") UserFormDTO form,BindingResult errors,@AuthenticationPrincipal CustomUserDetails actor,RedirectAttributes redirect) {
        form.setId(id); return save(id,form,errors,actor,redirect);
    }
    private String save(Long id,UserFormDTO form,BindingResult errors,CustomUserDetails actor,RedirectAttributes redirect) {
        if (errors.hasErrors()) return "users/form";
        try { users.save(id,form,actor.getId()); }
        catch (BusinessException ex) { errors.reject("user",ex.getMessage()); return "users/form"; }
        catch (org.springframework.dao.DataIntegrityViolationException ex) { errors.reject("duplicate","Email hoặc tên đăng nhập đã được sử dụng."); return "users/form"; }
        redirect.addFlashAttribute("success","Đã lưu người dùng."); return "redirect:/users";
    }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,@AuthenticationPrincipal CustomUserDetails actor,RedirectAttributes redirect) {
        try { users.delete(id,actor.getId()); redirect.addFlashAttribute("success","Đã xóa người dùng."); }
        catch (BusinessException ex) { redirect.addFlashAttribute("failure",ex.getMessage()); }
        return "redirect:/users";
    }
}
