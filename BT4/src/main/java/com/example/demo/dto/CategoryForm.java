package com.example.demo.dto;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
public record CategoryForm(Long categoryId,
    @NotBlank(message = "Tên danh mục không được để trống") @Size(max = 150) String categoryName,
    MultipartFile icon) {}
