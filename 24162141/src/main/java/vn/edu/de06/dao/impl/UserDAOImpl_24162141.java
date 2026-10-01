package vn.edu.de06.dao.impl;
import vn.edu.de06.dao.UserDAO_24162141;
import vn.edu.de06.model.User_24162141;
import vn.edu.de06.config.DBConnection_24162141;
import java.sql.*;
import java.util.*;
public class UserDAOImpl_24162141 implements UserDAO_24162141 {
    private static final String SELECT="SELECT u.*, r.roleName, s.sellername FROM Users u LEFT JOIN UserRoles r ON u.roleId=r.roleId LEFT JOIN Seller s ON u.sellerId=s.sellerId";
    private User_24162141 map(ResultSet r) throws SQLException {
        User_24162141 v=new User_24162141();
        v.setUserId(r.getInt("userId"));
        v.setUsername(r.getString("username"));
        v.setEmail(r.getString("email"));
        v.setFullname(r.getString("fullname"));
        v.setPassword(r.getString("password"));
        v.setImages(r.getString("images"));
        v.setPhone(r.getString("phone"));
        v.setStatus(r.getInt("status"));
        v.setCode(r.getString("code"));
        v.setRoleId(r.getInt("roleId"));
        v.setSellerId(r.getObject("sellerId", Integer.class));
        v.setRoleName(r.getString("roleName"));
        v.setSellername(r.getString("sellername"));
        return v;
    }
    public List<User_24162141> findAll(int page,int pageSize) throws SQLException {
        List<User_24162141> list=new ArrayList<>();
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(SELECT+" ORDER BY u.userId OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
            p.setInt(1,(page-1)*pageSize); p.setInt(2,pageSize);
            try(ResultSet r=p.executeQuery()) { while(r.next()) list.add(map(r)); }
        } return list;
    }
    public int count() throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM Users"); ResultSet r=p.executeQuery()) { r.next(); return r.getInt(1); }
    }
    public User_24162141 findById(int id) throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(SELECT+" WHERE u.userId=?")) {
            p.setInt(1,id); try(ResultSet r=p.executeQuery()) { return r.next()?map(r):null; }
        }
    }
    public void save(User_24162141 v) throws SQLException {
        boolean create=v.getUserId()==0;
        String sql=create?"INSERT INTO Users (username,email,fullname,password,images,phone,status,code,roleId,sellerId) VALUES (?,?,?,?,?,?,?,?,?,?)":"UPDATE Users SET username=?,email=?,fullname=?,password=?,images=?,phone=?,status=?,code=?,roleId=?,sellerId=? WHERE userId=?";
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            p.setObject(1, v.getUsername());
            p.setObject(2, v.getEmail());
            p.setObject(3, v.getFullname());
            p.setObject(4, v.getPassword());
            p.setObject(5, v.getImages());
            p.setObject(6, v.getPhone());
            p.setObject(7, v.getStatus());
            p.setObject(8, v.getCode());
            p.setObject(9, v.getRoleId());
            p.setObject(10, v.getSellerId());
            if(!create) p.setInt(11,v.getUserId());
            if(p.executeUpdate()!=1) throw new IllegalArgumentException("Bản ghi không còn tồn tại.");
            if(create) try(ResultSet r=p.getGeneratedKeys()) { if(r.next()) v.setUserId(r.getInt(1)); }
        }
    }
    public boolean delete(int id) throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement("DELETE FROM Users WHERE userId=?")) { p.setInt(1,id); return p.executeUpdate()==1; }
    }

    public User_24162141 findByUsername(String username) throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement(SELECT+" WHERE u.username=?")) {
            p.setString(1,username); try(ResultSet r=p.executeQuery()) { return r.next()?map(r):null; }
        }
    }
    public boolean exists(String username,String email,int excludeId) throws SQLException {
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM Users WHERE (username=? OR email=?) AND userId<>?")) {
            p.setString(1,username); p.setString(2,email); p.setInt(3,excludeId);
            try(ResultSet r=p.executeQuery()) { r.next(); return r.getInt(1)>0; }
        }
    }
}
