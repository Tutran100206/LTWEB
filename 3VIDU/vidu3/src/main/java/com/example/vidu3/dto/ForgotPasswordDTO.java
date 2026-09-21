package com.example.vidu3.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
@Getter @Setter
public class ForgotPasswordDTO {
@NotBlank(message="Vui lòng nhập email") @Email(message="Email không hợp lệ") @Size(max=254) private String email;

}
