package vn.edu.de06.dao;
import vn.edu.de06.model.Product_24162141;
import java.sql.SQLException;
import java.util.List;
public interface ProductDAO_24162141 {
    List<Product_24162141> findAll() throws SQLException;
    Product_24162141 findById(int id) throws SQLException;
}
