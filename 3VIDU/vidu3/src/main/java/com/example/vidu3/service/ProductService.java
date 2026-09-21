package com.example.vidu3.service;

import com.example.vidu3.dto.ProductDTO;
import com.example.vidu3.entity.Product;
import com.example.vidu3.mapper.ProductMapper;
import com.example.vidu3.repository.*;
import com.example.vidu3.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;
@Service @RequiredArgsConstructor
public class ProductService {
    private final ProductRepository products;
    private final UserRepository users;
    private final ProductMapper mapper;
    private final CloudinaryService cloudinary;
    private final ImageLifecycle images;
    @Transactional(readOnly=true) public Page<ProductDTO> search(String keyword, int page, int size, CustomUserDetails actor) {
        return products.search(keyword,actor.isAdmin()?null:actor.getId(),PageRequest.of(Math.max(0,page),Math.clamp(size,1,50),Sort.by("id").descending())).map(mapper::toDto);
    }
    @Transactional(readOnly=true) public ProductDTO get(Long id, CustomUserDetails actor) { return mapper.toDto(owned(id,actor)); }
    @Transactional public void save(Long id, ProductDTO form, MultipartFile file, CustomUserDetails actor) {
        Product product=id==null ? new Product() : owned(id,actor);
        mapper.update(form,product);
        Long owner=actor.isAdmin() && form.getUserId()!=null ? form.getUserId() : actor.getId();
        if (id==null || actor.isAdmin()) product.setUser(users.findById(owner).orElseThrow(() -> new BusinessException("Chủ sở hữu không tồn tại.")));
        CloudinaryUploadResult uploaded=cloudinary.upload(file);
        if (uploaded!=null) {
            images.rollbackDelete(uploaded.publicId()); images.afterCommitDelete(product.getImagePublicId());
            product.setImageUrl(uploaded.url()); product.setImagePublicId(uploaded.publicId());
        }
        products.saveAndFlush(product);
    }
    @Transactional public void delete(Long id, CustomUserDetails actor) {
        Product product=owned(id,actor); images.afterCommitDelete(product.getImagePublicId()); products.delete(product);
    }
    private Product owned(Long id, CustomUserDetails actor) {
        Product product=products.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,"Sản phẩm không tồn tại."));
        if (!actor.isAdmin() && !product.getUser().getId().equals(actor.getId())) throw new AccessDeniedException("Bạn không có quyền sửa sản phẩm này.");
        return product;
    }
}
