package com.example.vidu3.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
@Getter @Setter
public class ResetPasswordDTO {
@NotBlank(message="Vui lòng nhập email") @Email(message="Email không hợp lệ") @Size(max=254) private String email;
@NotBlank @Pattern(regexp="[0-9]{6}",message="OTP gồm 6 chữ số") private String code;
@NotBlank(message="Vui lòng nhập mật khẩu") @Size(min=8,max=72,message="Mật khẩu dài 8–72 ký tự") private String password;
@NotBlank(message="Vui lòng xác nhận mật khẩu") private String confirmPassword;
@AssertTrue(message="Mật khẩu xác nhận không khớp")
    public boolean isPasswordMatching() { return password != null && password.equals(confirmPassword); }
    @AssertTrue(message="Mật khẩu không được vượt quá 72 byte UTF-8")
    public boolean isPasswordWithinBytes() { return password == null || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72; }
}
