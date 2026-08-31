package vn.iotstar.service.impl;

import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.dao.impl.CategoryDao;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;

import java.util.List;

public class CategoryServiceImpl implements ICategoryService {
    private final ICategoryDao cateDao = new CategoryDao();

    @Override
    public void insert(Category category) {
        Category existing = cateDao.findByCategoryname(category.getCategoryname());
        if (existing == null) {
            cateDao.insert(category);
        }
    }

    @Override
    public void update(Category category) {
        Category current = cateDao.findById(category.getCategoryid());
        if (current == null) {
            throw new IllegalArgumentException("Không tìm thấy category id = " + category.getCategoryid());
        }
        cateDao.update(category);
    }

    @Override
    public void delete(int cateid) {
        try {
            cateDao.delete(cateid);
        } catch (Exception e) {
            throw new RuntimeException("Xóa category thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    public Category findById(int cateid) {
        return cateDao.findById(cateid);
    }

    @Override
    public Category findByCategoryname(String name) {
        return cateDao.findByCategoryname(name);
    }

    @Override
    public List<Category> findAll() {
        return cateDao.findAll();
    }

    @Override
    public List<Category> searchByName(String catname) {
        return cateDao.searchByName(catname);
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        return cateDao.findAll(page, pagesize);
    }

    @Override
    public int count() {
        return cateDao.count();
    }
}
