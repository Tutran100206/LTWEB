package com.example.vidu3.mapper;

import com.example.vidu3.entity.Product;
import com.example.vidu3.dto.ProductDTO;
import org.mapstruct.*;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface ProductMapper {
    @Mapping(target="userId",source="user.id") @Mapping(target="username",source="user.username")
    ProductDTO toDto(Product product);
    @Mapping(target="id",ignore=true) @Mapping(target="user",ignore=true)
    @Mapping(target="imageUrl",ignore=true) @Mapping(target="imagePublicId",ignore=true)
    @Mapping(target="createdAt",ignore=true)
    void update(ProductDTO dto, @MappingTarget Product product);
}
