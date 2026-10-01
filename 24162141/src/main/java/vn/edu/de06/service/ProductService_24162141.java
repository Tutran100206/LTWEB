package vn.edu.de06.service;
import vn.edu.de06.dao.*;
import vn.edu.de06.dao.impl.*;
import vn.edu.de06.model.*;
import java.sql.SQLException;
import java.util.*;
public class ProductService_24162141 {
    private final ProductDAO_24162141 dao=new ProductDAOImpl_24162141();
    public Map<Integer,List<Product_24162141>> groupedBySeller() throws SQLException {
        Map<Integer,List<Product_24162141>> groups=new LinkedHashMap<>();
        for(Product_24162141 p:dao.findAll()) groups.computeIfAbsent(p.getSellerId(), k->new ArrayList<>()).add(p);
        return groups;
    }
    public Product_24162141 findById(int id) throws SQLException { return dao.findById(id); }
}
