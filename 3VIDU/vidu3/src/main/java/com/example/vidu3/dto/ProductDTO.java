package com.example.vidu3.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
@Getter @Setter
public class ProductDTO {
private Long id;
    @NotBlank(message="Vui lòng nhập tên sản phẩm") @Size(max=150) private String name;
    @Size(max=2000) private String description;
    @NotNull(message="Vui lòng nhập giá") @DecimalMin(value="0.0",message="Giá không được âm") @Digits(integer=16,fraction=2) private java.math.BigDecimal price;
    private String imageUrl;
    private java.time.LocalDateTime createdAt;
    private Long userId;
    private String username;

}
