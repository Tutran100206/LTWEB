package vn.edu.de06.dao.impl;
import vn.edu.de06.dao.ProductDAO_24162141;
import vn.edu.de06.model.Product_24162141;
import vn.edu.de06.config.DBConnection_24162141;
import java.sql.*;
import java.util.*;
public class ProductDAOImpl_24162141 implements ProductDAO_24162141 {
    private static final String SELECT="SELECT p.*,c.categoryName,s.sellername FROM Product p LEFT JOIN Category c ON p.categoryId=c.categoryId LEFT JOIN Seller s ON p.sellerId=s.sellerId";
    private Product_24162141 map(ResultSet r) throws SQLException {
        Product_24162141 v=new Product_24162141();
        v.setProductId(r.getInt("productId"));
        v.setProductName(r.getString("productName"));
        v.setProductCode(r.getLong("productCode"));
        v.setCategoryId(r.getInt("categoryId"));
        v.setDescription(r.getString("description"));
        v.setPrice(r.getDouble("price"));
        v.setAmount(r.getInt("amount"));
        v.setStock(r.getInt("stock"));
        v.setImages(r.getString("images"));
        v.setWishlist(r.getInt("wishlist"));
        v.setStatus(r.getInt("status"));
        v.setCreateDate(r.getDate("createDate"));
        v.setSellerId(r.getObject("sellerId", Integer.class));
        v.setCategoryName(r.getString("categoryName"));
        v.setSellername(r.getString("sellername"));
        return v;
    }
    public List<Product_24162141> findAll() throws SQLException {
        List<Product_24162141> list=new ArrayList<>();
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(SELECT+" ORDER BY p.sellerId,p.productId"); ResultSet r=p.executeQuery()) {
            while(r.next()) list.add(map(r));
        } return list;
    }
    public Product_24162141 findById(int id) throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(SELECT+" WHERE p.productId=?")) {
            p.setInt(1,id); try(ResultSet r=p.executeQuery()) { return r.next()?map(r):null; }
        }
    }
}
