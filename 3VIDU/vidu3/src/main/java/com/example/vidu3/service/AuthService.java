package com.example.vidu3.service;

import com.example.vidu3.dto.*;
import com.example.vidu3.entity.*;
import com.example.vidu3.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Locale;
@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final OtpService otp;
    public static String normalize(String value) { return value.strip().toLowerCase(Locale.ROOT); }
    @Transactional public void register(RegisterDTO form) {
        String email=normalize(form.getEmail()), username=normalize(form.getUsername());
        if (users.existsByEmail(email) || users.existsByUsername(username)) throw new BusinessException("Email hoặc tên đăng nhập đã được sử dụng.");
        User user=new User(); user.setEmail(email); user.setUsername(username); user.setFullName(form.getFullName().strip());
        user.setPassword(encoder.encode(form.getPassword())); user.setEnabled(false);
        user.setRole(roles.findByName("USER").orElseThrow(() -> new BusinessException("Chưa cấu hình vai trò USER.")));
        users.saveAndFlush(user); otp.issue(email,OtpType.REGISTER);
    }
    @Transactional(noRollbackFor=InvalidOtpException.class) public void verify(VerifyOtpDTO form) {
        User user=locked(form.getEmail());
        if (user.isEnabled()) throw new BusinessException("Tài khoản đã được kích hoạt. Bạn có thể đăng nhập.");
        otp.consume(user.getEmail(),OtpType.REGISTER,form.getCode()); user.setEnabled(true);
    }
    @Transactional public void resend(String email) {
        var user=users.lockByEmail(normalize(email));
        if (user.isPresent() && !user.get().isEnabled()) otp.issue(user.get().getEmail(),OtpType.REGISTER);
    }
    @Transactional public void forgot(String email) {
        var user=users.lockByEmail(normalize(email));
        if (user.isPresent() && user.get().isEnabled()) otp.issue(user.get().getEmail(),OtpType.RESET_PASSWORD);
    }
    @Transactional(noRollbackFor=InvalidOtpException.class) public void reset(ResetPasswordDTO form) {
        User user=locked(form.getEmail());
        if (!user.isEnabled()) throw new BusinessException("Không thể đặt lại mật khẩu cho tài khoản này.");
        otp.consume(user.getEmail(),OtpType.RESET_PASSWORD,form.getCode()); user.setPassword(encoder.encode(form.getPassword()));
    }
    private User locked(String email) { return users.lockByEmail(normalize(email)).orElseThrow(() -> new InvalidOtpException("Email hoặc OTP không hợp lệ.")); }
}
