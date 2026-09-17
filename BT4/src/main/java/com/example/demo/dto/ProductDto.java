package com.example.demo.dto;
import com.example.demo.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record ProductDto(Long productId, String productName, Integer quantity,
    BigDecimal unitPrice, String images, String description, BigDecimal discount,
    LocalDateTime createDate, Boolean status, Long categoryId, String categoryName) {
    public static ProductDto from(Product p) {
        return new ProductDto(p.getProductId(), p.getProductName(), p.getQuantity(),
            p.getUnitPrice(), p.getImages(), p.getDescription(), p.getDiscount(),
            p.getCreateDate(), p.getStatus(), p.getCategory().getCategoryId(),
            p.getCategory().getCategoryName());
    }
}
