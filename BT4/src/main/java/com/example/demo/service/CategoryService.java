package com.example.demo.service;
import com.example.demo.dto.*;
import com.example.demo.entity.Category;
import com.example.demo.repository.*;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final StorageService storage;
    public CategoryService(CategoryRepository categories, ProductRepository products, StorageService storage) {
        this.categories = categories; this.products = products; this.storage = storage;
    }
    @Transactional(readOnly = true)
    public List<CategoryDto> all() {
        return categories.findAll(Sort.by("categoryId").descending()).stream().map(CategoryDto::from).toList();
    }
    public Category require(Long id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu categoryId");
        return categories.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Danh mục không tồn tại"));
    }
    @Transactional(readOnly = true)
    public CategoryDto get(Long id) { return CategoryDto.from(require(id)); }
    public CategoryDto save(CategoryForm form, boolean update) {
        Category c = update ? require(form.categoryId()) : new Category();
        String name = form.categoryName().trim();
        if (update ? categories.existsByCategoryNameIgnoreCaseAndCategoryIdNot(name, c.getCategoryId())
                   : categories.existsByCategoryNameIgnoreCase(name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên danh mục đã tồn tại");
        c.setCategoryName(name);
        c.setIcon(storage.replace(form.icon(), c.getIcon()));
        return CategoryDto.from(categories.saveAndFlush(c));
    }
    public void delete(Long id) {
        Category c = require(id);
        if (products.existsByCategoryCategoryId(id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Danh mục đang có sản phẩm, hãy chuyển hoặc xóa sản phẩm trước");
        categories.delete(c);
        categories.flush();
        storage.deleteAfterCommit(c.getIcon());
    }
}
