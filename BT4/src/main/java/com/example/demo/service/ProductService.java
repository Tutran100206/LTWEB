package com.example.demo.service;
import com.example.demo.dto.*;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ProductService {
    private final ProductRepository products;
    private final CategoryService categories;
    private final StorageService storage;
    public ProductService(ProductRepository products, CategoryService categories, StorageService storage) {
        this.products = products; this.categories = categories; this.storage = storage;
    }
    private Product require(Long id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu productId");
        return products.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));
    }
    @Transactional(readOnly = true)
    public List<ProductDto> all() {
        return products.findAll(Sort.by("productId").descending()).stream().map(ProductDto::from).toList();
    }
    @Transactional(readOnly = true)
    public ProductDto get(Long id) { return ProductDto.from(require(id)); }
    public ProductDto save(ProductForm form, boolean update) {
        Product p = update ? require(form.productId()) : new Product();
        p.setCategory(categories.require(form.categoryId()));
        p.setProductName(form.productName().trim());
        p.setQuantity(form.quantity());
        p.setUnitPrice(form.unitPrice());
        p.setDiscount(form.discount());
        p.setDescription(form.description());
        p.setStatus(form.status());
        if (!update) p.setCreateDate(LocalDateTime.now());
        p.setImages(storage.replace(form.imageFile(), p.getImages()));
        return ProductDto.from(products.saveAndFlush(p));
    }
    public void delete(Long id) {
        Product p = require(id);
        products.delete(p); products.flush();
        storage.deleteAfterCommit(p.getImages());
    }
}
