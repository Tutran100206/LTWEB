package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryApiController {
    private final CategoryService service;
    public CategoryApiController(CategoryService service) { this.service = service; }
    @GetMapping
    public ApiResponse<List<CategoryDto>> all() { return ApiResponse.ok("Thành công", service.all()); }
    @GetMapping("/{id}")
    public ApiResponse<CategoryDto> get(@PathVariable Long id) { return ApiResponse.ok("Thành công", service.get(id)); }
    @PostMapping(value = "/addCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryDto> add(@Valid @ModelAttribute CategoryForm form) {
        return ApiResponse.ok("Đã thêm danh mục", service.save(form, false));
    }
    @PutMapping(value = "/updateCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CategoryDto> update(@Valid @ModelAttribute CategoryForm form) {
        return ApiResponse.ok("Đã cập nhật danh mục", service.save(form, true));
    }
    @DeleteMapping("/deleteCategory")
    public ApiResponse<Void> delete(@RequestParam Long categoryId) {
        service.delete(categoryId); return ApiResponse.ok("Đã xóa danh mục", null);
    }
}
