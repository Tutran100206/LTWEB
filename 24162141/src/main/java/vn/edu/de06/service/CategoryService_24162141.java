package vn.edu.de06.service;
import vn.edu.de06.dao.*;
import vn.edu.de06.dao.impl.*;
import vn.edu.de06.model.*;
import vn.edu.de06.util.Validation_24162141;
import java.sql.SQLException;
import java.util.List;
public class CategoryService_24162141 {
    private final CategoryDAO_24162141 dao=new CategoryDAOImpl_24162141();
    public int count() throws SQLException { return dao.count(); }
    public List<Category_24162141> findAll(int page,int size) throws SQLException { return dao.findAll(page,size); }
    public Category_24162141 findById(int id) throws SQLException { return dao.findById(id); }
    public void save(Category_24162141 v) throws SQLException {
        v.setCategoryName(Validation_24162141.text(v.getCategoryName(),"Tên danh mục",200,true));
        v.setImages(Validation_24162141.image(v.getImages())); dao.save(v);
    }
    public void delete(int id) throws SQLException { if(!dao.delete(id)) throw new IllegalArgumentException("Danh mục không tồn tại."); }
}
