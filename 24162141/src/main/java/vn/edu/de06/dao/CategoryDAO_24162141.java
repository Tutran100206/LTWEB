package vn.edu.de06.dao;
import vn.edu.de06.model.Category_24162141;
import java.sql.SQLException;
import java.util.List;
public interface CategoryDAO_24162141 {
    List<Category_24162141> findAll(int page, int pageSize) throws SQLException;
    int count() throws SQLException;
    Category_24162141 findById(int id) throws SQLException;
    void save(Category_24162141 value) throws SQLException;
    boolean delete(int id) throws SQLException;
}
