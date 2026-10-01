package vn.edu.de06.dao.impl;
import vn.edu.de06.dao.LookupDAO_24162141;
import vn.edu.de06.model.*;
import vn.edu.de06.config.DBConnection_24162141;
import java.sql.*;
import java.util.*;
public class LookupDAOImpl_24162141 implements LookupDAO_24162141 {
    public List<UserRole_24162141> roles() throws SQLException {
        List<UserRole_24162141> list=new ArrayList<>();
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement("SELECT * FROM UserRoles ORDER BY roleId"); ResultSet r=p.executeQuery()) {
            while(r.next()) { UserRole_24162141 v=new UserRole_24162141(); v.setRoleId(r.getInt("roleId")); v.setRoleName(r.getString("roleName")); list.add(v); }
        } return list;
    }
    public List<Seller_24162141> sellers() throws SQLException {
        List<Seller_24162141> list=new ArrayList<>();
        try(Connection c=DBConnection_24162141.open(); PreparedStatement p=c.prepareStatement("SELECT * FROM Seller ORDER BY sellerId"); ResultSet r=p.executeQuery()) {
            while(r.next()) { Seller_24162141 v=new Seller_24162141(); v.setSellerId(r.getInt("sellerId")); v.setSellername(r.getString("sellername")); v.setImages(r.getString("images")); v.setStatus(r.getInt("status")); list.add(v); }
        } return list;
    }
}
