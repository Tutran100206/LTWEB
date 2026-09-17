package com.example.demo.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;
public record ProductForm(Long productId,
    @NotBlank(message = "Tên sản phẩm không được để trống") @Size(max = 150) String productName,
    @NotNull @PositiveOrZero Integer quantity,
    @NotNull @DecimalMin("0.0") @Digits(integer = 16, fraction = 2) BigDecimal unitPrice,
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0") @Digits(integer = 3, fraction = 2) BigDecimal discount,
    @Size(max = 4000) String description,
    @NotNull @Positive Long categoryId,
    @NotNull Boolean status,
    MultipartFile imageFile) {}
