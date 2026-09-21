package com.example.vidu3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender sender;
    private final String from;
    public EmailServiceImpl(JavaMailSender sender, @Value("${spring.mail.username}") String from) { this.sender=sender; this.from=from; }
    @Override public void sendOtp(String email, String code, String purpose) {
        if (from.isBlank()) throw new BusinessException("Chưa cấu hình email gửi. Vui lòng liên hệ quản trị viên.");
        var mail=new SimpleMailMessage(); mail.setFrom(from); mail.setTo(email);
        mail.setSubject("3VIDU — Mã xác thực " + purpose);
        mail.setText("Xin chào,\n\nMã OTP của bạn: " + code + "\nMã có hiệu lực 5 phút và chỉ sử dụng một lần. Không chia sẻ OTP với bất kỳ ai.\n\nNếu bạn không yêu cầu, hãy bỏ qua email này.");
        try { sender.send(mail); } catch (org.springframework.mail.MailException ex) { throw new BusinessException("Không thể gửi email lúc này. Vui lòng thử lại sau."); }
    }
}
