package com.example.vidu3.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
@Getter @Setter
public class UserFormDTO {
private Long id;
@NotBlank(message="Vui lòng nhập tên đăng nhập") @Pattern(regexp="[a-zA-Z0-9_.-]{3,50}", message="Tên đăng nhập gồm 3–50 chữ, số hoặc dấu _ . -") private String username;
@NotBlank(message="Vui lòng nhập email") @Email(message="Email không hợp lệ") @Size(max=254) private String email;
@NotBlank(message="Vui lòng nhập họ tên") @Size(max=100) private String fullName;
private String password;
@Pattern(regexp="USER|ADMIN",message="Vai trò không hợp lệ") @NotBlank private String roleName="USER";
private boolean enabled=true;
@AssertTrue(message="Mật khẩu mới dài 8–72 byte; có thể để trống khi sửa")
    public boolean isPasswordValid() {
        if (password == null || password.isBlank()) return id != null;
        return password.length() >= 8 && password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72;
    }
}
