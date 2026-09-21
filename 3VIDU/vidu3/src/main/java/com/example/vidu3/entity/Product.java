package com.example.vidu3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="vd3_products") @Getter @Setter
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, columnDefinition="nvarchar(150)") private String name;
    @Column(columnDefinition="nvarchar(2000)") private String description;
    @Column(nullable=false, precision=18, scale=2) private BigDecimal price;
    @Column(length=1000) private String imageUrl;
    @Column(length=255) private String imagePublicId;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt=LocalDateTime.now();
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false) private User user;
}
