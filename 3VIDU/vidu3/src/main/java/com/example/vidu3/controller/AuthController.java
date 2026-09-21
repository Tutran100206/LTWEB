package com.example.vidu3.controller;

import com.example.vidu3.dto.*;
import com.example.vidu3.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;
    @GetMapping("/register") public String register(Model model) { model.addAttribute("form",new RegisterDTO()); return "register"; }
    @PostMapping("/register") public String register(@Valid @ModelAttribute("form") RegisterDTO form,BindingResult errors,RedirectAttributes redirect) {
        if (errors.hasErrors()) return "register";
        try { auth.register(form); }
        catch (BusinessException ex) { errors.reject("register",ex.getMessage()); return "register"; }
        catch (org.springframework.dao.DataIntegrityViolationException ex) { errors.reject("duplicate","Email hoặc tên đăng nhập đã được sử dụng."); return "register"; }
        redirect.addAttribute("email",AuthService.normalize(form.getEmail())); redirect.addFlashAttribute("success","Đã gửi OTP. Vui lòng kiểm tra email."); return "redirect:/verify-otp";
    }
    @GetMapping("/verify-otp") public String verify(@RequestParam(defaultValue="") String email,Model model) { var form=new VerifyOtpDTO(); form.setEmail(email); model.addAttribute("form",form); return "verify-otp"; }
    @PostMapping("/verify-otp") public String verify(@Valid @ModelAttribute("form") VerifyOtpDTO form,BindingResult errors,RedirectAttributes redirect) {
        if (errors.hasErrors()) return "verify-otp";
        try { auth.verify(form); } catch (BusinessException ex) { errors.reject("otp",ex.getMessage()); return "verify-otp"; }
        redirect.addFlashAttribute("success","Tài khoản đã được kích hoạt. Mời bạn đăng nhập."); return "redirect:/login";
    }
    @PostMapping("/resend-register-otp") public String resend(@Valid @ModelAttribute("form") ForgotPasswordDTO form,BindingResult errors,RedirectAttributes redirect) {
        redirect.addAttribute("email",form.getEmail());
        if (errors.hasErrors()) redirect.addFlashAttribute("failure","Vui lòng nhập email hợp lệ trước khi gửi lại OTP.");
        else {
            try { auth.resend(form.getEmail()); redirect.addFlashAttribute("success","Nếu tài khoản đang chờ kích hoạt, mã mới đã được gửi. Vui lòng chờ ít nhất 60 giây giữa các yêu cầu."); }
            catch (BusinessException ex) { redirect.addFlashAttribute("failure",ex.getMessage()); }
        }
        return "redirect:/verify-otp";
    }
    @GetMapping("/forgot-password") public String forgot(Model model) { model.addAttribute("form",new ForgotPasswordDTO()); return "forgot-password"; }
    @PostMapping("/forgot-password") public String forgot(@Valid @ModelAttribute("form") ForgotPasswordDTO form,BindingResult errors,RedirectAttributes redirect) {
        if (errors.hasErrors()) return "forgot-password";
        // Identical response for absent, disabled and existing accounts, including delivery/cooldown failures.
        try { auth.forgot(form.getEmail()); } catch (BusinessException ignored) { }
        redirect.addAttribute("email",AuthService.normalize(form.getEmail()));
        redirect.addFlashAttribute("success","Nếu email thuộc tài khoản đang hoạt động, bạn sẽ nhận được OTP. Kiểm tra cả thư rác; chờ 60 giây trước khi yêu cầu lại."); return "redirect:/reset-password";
    }
    @GetMapping("/reset-password") public String reset(@RequestParam(defaultValue="") String email,Model model) { var form=new ResetPasswordDTO(); form.setEmail(email); model.addAttribute("form",form); return "reset-password"; }
    @PostMapping("/reset-password") public String reset(@Valid @ModelAttribute("form") ResetPasswordDTO form,BindingResult errors,RedirectAttributes redirect) {
        if (errors.hasErrors()) return "reset-password";
        try { auth.reset(form); } catch (BusinessException ex) { errors.reject("otp",ex.getMessage()); return "reset-password"; }
        redirect.addFlashAttribute("success","Đã đổi mật khẩu. Mời bạn đăng nhập bằng mật khẩu mới."); return "redirect:/login";
    }
}
