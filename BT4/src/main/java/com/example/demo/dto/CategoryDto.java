package com.example.demo.dto;
import com.example.demo.entity.Category;
public record CategoryDto(Long categoryId, String categoryName, String icon) {
    public static CategoryDto from(Category c) {
        return new CategoryDto(c.getCategoryId(), c.getCategoryName(), c.getIcon());
    }
}
