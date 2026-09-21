package com.example.vidu3.service;

import com.example.vidu3.entity.*;
import com.example.vidu3.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Clock;
import java.security.SecureRandom;
@Service @RequiredArgsConstructor
public class OtpService {
    private final OtpTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final Clock clock;
    private final SecureRandom random=new SecureRandom();
    // Called within AuthService's transaction, after locking the user row.
    public void issue(String email, OtpType type) {
        var now=clock.instant();
        tokens.findFirstByEmailAndTypeOrderByIdDesc(email,type).ifPresent(last -> {
            if (last.getCreatedAt().plusSeconds(60).isAfter(now)) throw new BusinessException("Vui lòng chờ 60 giây trước khi yêu cầu mã mới.");
        });
        tokens.findByEmailAndTypeAndUsedFalse(email,type).forEach(old -> old.setUsed(true));
        String code=String.format(java.util.Locale.ROOT,"%06d",random.nextInt(1_000_000));
        OtpToken token=new OtpToken(); token.setEmail(email); token.setType(type); token.setCodeHash(encoder.encode(code));
        token.setCreatedAt(now); token.setExpiresAt(now.plusSeconds(300)); tokens.save(token);
        emailService.sendOtp(email,code,type==OtpType.REGISTER ? "đăng ký tài khoản" : "đặt lại mật khẩu");
    }
    public void consume(String email, OtpType type, String code) {
        OtpToken token=tokens.findFirstByEmailAndTypeOrderByIdDesc(email,type).orElseThrow(() -> new InvalidOtpException("OTP không hợp lệ hoặc chưa được yêu cầu."));
        if (token.isUsed()) throw new InvalidOtpException("OTP đã được sử dụng hoặc vô hiệu hóa. Hãy yêu cầu mã mới.");
        if (!token.getExpiresAt().isAfter(clock.instant())) throw new InvalidOtpException("OTP đã hết hạn. Hãy yêu cầu mã mới.");
        token.setAttempts(token.getAttempts()+1);
        if (!encoder.matches(code,token.getCodeHash())) {
            if (token.getAttempts()>=5) token.setUsed(true);
            throw new InvalidOtpException("OTP không đúng. Sau 5 lần sai, bạn cần yêu cầu mã mới.");
        }
        token.setUsed(true);
    }
}
