package vn.edu.de06.service;

import vn.edu.de06.dao.impl.CartDAOImpl_24162141;
import vn.edu.de06.model.*;
import java.sql.SQLException;
import java.util.List;

public class CartService_24162141 {
    private final CartDAOImpl_24162141 dao=new CartDAOImpl_24162141();
    public Cart_24162141 cart(int userId) throws SQLException { return dao.getCart(userId); }
    public List<Cart_24162141> orders(int userId,Integer status) throws SQLException { return dao.orders(userId,status); }
    public static int positive(String value) {
        try { int n=Integer.parseInt(value); if(n>0) return n; } catch(NumberFormatException ignored) {}
        throw new IllegalArgumentException("Số lượng và mã sản phẩm phải là số nguyên dương hợp lệ.");
    }
    public static Integer status(String value) {
        if(value==null || value.isBlank()) return null;
        int n=positive(value); if(n>8) throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ."); return n;
    }
    public void change(int userId,String action,String productId,String quantity) throws SQLException {
        if(!java.util.Arrays.asList("add","update","remove","clear").contains(action)) throw new IllegalArgumentException("Thao tác giỏ hàng không hợp lệ.");
        int id="clear".equals(action)?0:positive(productId);
        int qty="add".equals(action)||"update".equals(action)?positive(quantity):0;
        dao.change(userId,id,qty,action);
    }
    public static String text(String value,int max,String label) {
        String s=value==null?"":value.trim();
        if(s.isEmpty() || s.length()>max) throw new IllegalArgumentException(label+" bắt buộc và không được quá "+max+" ký tự."); return s;
    }
    public String checkout(int userId,String cartId,String name,String phone,String address) throws SQLException {
        name=text(name,100,"Tên người nhận"); address=text(address,500,"Địa chỉ"); phone=text(phone,20,"Số điện thoại");
        if(!phone.matches("0[0-9]{9,10}")) throw new IllegalArgumentException("Số điện thoại phải gồm 10–11 chữ số, bắt đầu bằng 0.");
        return dao.checkout(userId,cartId,name,phone,address);
    }
}
