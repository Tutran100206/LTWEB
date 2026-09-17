package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductApiController {
    private final ProductService service;
    public ProductApiController(ProductService service) { this.service = service; }
    @GetMapping
    public ApiResponse<List<ProductDto>> all() { return ApiResponse.ok("Thành công", service.all()); }
    @GetMapping("/{id}")
    public ApiResponse<ProductDto> get(@PathVariable Long id) { return ApiResponse.ok("Thành công", service.get(id)); }
    @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductDto> add(@Valid @ModelAttribute ProductForm form) {
        return ApiResponse.ok("Đã thêm sản phẩm", service.save(form, false));
    }
    @PutMapping(value = "/updateProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductDto> update(@Valid @ModelAttribute ProductForm form) {
        return ApiResponse.ok("Đã cập nhật sản phẩm", service.save(form, true));
    }
    @DeleteMapping("/deleteProduct")
    public ApiResponse<Void> delete(@RequestParam Long productId) {
        service.delete(productId); return ApiResponse.ok("Đã xóa sản phẩm", null);
    }
}
