package vn.edu.de06.dao.impl;
import vn.edu.de06.dao.CategoryDAO_24162141;
import vn.edu.de06.model.Category_24162141;
import vn.edu.de06.config.DBConnection_24162141;
import java.sql.*;
import java.util.*;
public class CategoryDAOImpl_24162141 implements CategoryDAO_24162141 {
    private static final String SELECT="SELECT * FROM Category";
    private Category_24162141 map(ResultSet r) throws SQLException {
        Category_24162141 v=new Category_24162141();
        v.setCategoryId(r.getInt("categoryId"));
        v.setCategoryName(r.getString("categoryName"));
        v.setImages(r.getString("images"));
        v.setStatus(r.getInt("status"));
        return v;
    }
    public List<Category_24162141> findAll(int page,int pageSize) throws SQLException {
        List<Category_24162141> list=new ArrayList<>();
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(SELECT+" ORDER BY categoryId OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
            p.setInt(1,(page-1)*pageSize); p.setInt(2,pageSize);
            try(ResultSet r=p.executeQuery()) { while(r.next()) list.add(map(r)); }
        } return list;
    }
    public int count() throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM Category"); ResultSet r=p.executeQuery()) { r.next(); return r.getInt(1); }
    }
    public Category_24162141 findById(int id) throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(SELECT+" WHERE categoryId=?")) {
            p.setInt(1,id); try(ResultSet r=p.executeQuery()) { return r.next()?map(r):null; }
        }
    }
    public void save(Category_24162141 v) throws SQLException {
        boolean create=v.getCategoryId()==0;
        String sql=create?"INSERT INTO Category (categoryName,images,status) VALUES (?,?,?)":"UPDATE Category SET categoryName=?,images=?,status=? WHERE categoryId=?";
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            p.setObject(1, v.getCategoryName());
            p.setObject(2, v.getImages());
            p.setObject(3, v.getStatus());
            if(!create) p.setInt(4,v.getCategoryId());
            if(p.executeUpdate()!=1) throw new IllegalArgumentException("Bản ghi không còn tồn tại.");
            if(create) try(ResultSet r=p.getGeneratedKeys()) { if(r.next()) v.setCategoryId(r.getInt(1)); }
        }
    }
    public boolean delete(int id) throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement("DELETE FROM Category WHERE categoryId=?")) { p.setInt(1,id); return p.executeUpdate()==1; }
    }
}
