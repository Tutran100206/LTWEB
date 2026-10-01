package vn.edu.de06.dao.impl;

import vn.edu.de06.config.DBConnection_24162141;
import vn.edu.de06.model.*;
import java.sql.*;
import java.util.*;

public class CartDAOImpl_24162141 {
    private PreparedStatement statement(Connection c,String sql,Object... args) throws SQLException {
        PreparedStatement p=c.prepareStatement(sql);
        for(int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
        return p;
    }
    private void execute(Connection c,String sql,Object... args) throws SQLException {
        try(PreparedStatement p=statement(c,sql,args)) { p.executeUpdate(); }
    }
    // All writes for one buyer serialize on the user row, including concurrent tabs.
    private void lockBuyer(Connection c,int userId) throws SQLException {
        try(PreparedStatement p=statement(c,"SELECT status FROM Users WITH (UPDLOCK,HOLDLOCK) WHERE userId=?",userId);ResultSet r=p.executeQuery()) {
            if(!r.next() || r.getInt(1)!=1) throw new IllegalArgumentException("Tài khoản không còn hoạt động.");
        }
    }
    private Cart_24162141 map(ResultSet r) throws SQLException {
        Cart_24162141 o=new Cart_24162141(); o.setCartId(r.getString("cartId")); o.setUserId(r.getInt("userId"));
        o.setStatus(r.getInt("status")); o.setBuyDate(r.getTimestamp("buyDate"));
        o.setRecipientName(r.getString("recipientName")); o.setPhone(r.getString("phone"));
        o.setAddress(r.getString("address")); o.setPaymentMethod(r.getString("paymentMethod")); return o;
    }
    private List<CartItem_24162141> items(Connection c,String cartId,boolean draft) throws SQLException {
        List<CartItem_24162141> list=new ArrayList<>();
        try(PreparedStatement p=statement(c,"SELECT i.*, p.productName AS currentName,p.price,p.stock,p.status AS productStatus FROM CartItem i JOIN Product p ON p.productId=i.productId WHERE i.cartId=? ORDER BY i.productId",cartId);ResultSet r=p.executeQuery()) {
            while(r.next()) {
                CartItem_24162141 i=new CartItem_24162141(); i.setCartItemId(r.getString("cartItemId")); i.setCartId(cartId);
                i.setProductId(r.getInt("productId")); i.setQuantity(r.getInt("quantity"));
                i.setUnitPrice(r.getDouble(draft?"price":"unitPrice"));
                i.setProductName(draft || r.getString("productName")==null?r.getString("currentName"):r.getString("productName"));
                i.setStock(r.getInt("stock")); i.setProductStatus(r.getInt("productStatus")); list.add(i);
            }
        } return list;
    }
    private Cart_24162141 draft(Connection c,int userId) throws SQLException {
        try(PreparedStatement p=statement(c,"SELECT TOP 1 * FROM Cart WHERE userId=? AND status=0 ORDER BY cartId",userId);ResultSet r=p.executeQuery()) {
            if(r.next()) return map(r);
        } return null;
    }
    public Cart_24162141 getCart(int userId) throws SQLException {
        try(Connection c=DBConnection_24162141.open()) {
            Cart_24162141 o=draft(c,userId);
            if(o==null) o=new Cart_24162141(); else o.setItems(items(c,o.getCartId(),true)); return o;
        }
    }
    public void change(int userId,int productId,int quantity,String action) throws SQLException {
        try(Connection c=DBConnection_24162141.open()) {
            c.setAutoCommit(false);
            try {
                lockBuyer(c,userId); Cart_24162141 cart=draft(c,userId);
                if("remove".equals(action) || "clear".equals(action)) {
                    if(cart!=null) {
                        if("clear".equals(action)) execute(c,"DELETE FROM CartItem WHERE cartId=?",cart.getCartId());
                        else execute(c,"DELETE FROM CartItem WHERE cartId=? AND productId=?",cart.getCartId(),productId);
                    }
                } else {
                    if(cart==null) {
                        if(!"add".equals(action)) throw new IllegalArgumentException("Sản phẩm không có trong giỏ hàng.");
                        cart=new Cart_24162141(); cart.setCartId(UUID.randomUUID().toString());
                        execute(c,"INSERT INTO Cart(cartId,userId,status) VALUES(?,?,0)",cart.getCartId(),userId);
                    }
                    int old=0;
                    try(PreparedStatement p=statement(c,"SELECT quantity FROM CartItem WHERE cartId=? AND productId=?",cart.getCartId(),productId);ResultSet r=p.executeQuery()) { if(r.next()) old=r.getInt(1); }
                    if("update".equals(action) && old==0) throw new IllegalArgumentException("Sản phẩm không có trong giỏ hàng.");
                    long desired="add".equals(action)?(long)old+quantity:quantity;
                    try(PreparedStatement p=statement(c,"SELECT price,stock,status FROM Product WHERE productId=?",productId);ResultSet r=p.executeQuery()) {
                        if(!r.next() || r.getInt("status")!=1) throw new IllegalArgumentException("Sản phẩm không còn được bán.");
                        if(desired<1 || desired>r.getInt("stock")) throw new IllegalArgumentException("Số lượng phải từ 1 đến tồn kho hiện tại ("+r.getInt("stock")+").");
                        if(old==0) execute(c,"INSERT INTO CartItem(quantity,unitPrice,productId,cartId) VALUES(?,?,?,?)",(int)desired,r.getDouble("price"),productId,cart.getCartId());
                        else execute(c,"UPDATE CartItem SET quantity=?,unitPrice=? WHERE cartId=? AND productId=?",(int)desired,r.getDouble("price"),cart.getCartId(),productId);
                    }
                }
                c.commit();
            } catch(SQLException|RuntimeException e) { c.rollback(); throw e; }
        }
    }
    public String checkout(int userId,String cartId,String name,String phone,String address) throws SQLException {
        try(Connection c=DBConnection_24162141.open()) {
            c.setAutoCommit(false);
            try {
                lockBuyer(c,userId); Cart_24162141 cart=draft(c,userId);
                if(cart==null || !cart.getCartId().equals(cartId)) throw new IllegalArgumentException("Giỏ hàng đã được đặt hoặc đã thay đổi. Vui lòng tải lại trang.");
                List<CartItem_24162141> list=items(c,cartId,true);
                if(list.isEmpty()) throw new IllegalArgumentException("Giỏ hàng đang trống.");
                for(CartItem_24162141 i:list) {
                    // Conditional update prevents overselling even across different buyers.
                    try(PreparedStatement p=statement(c,"UPDATE Product SET stock=stock-? WHERE productId=? AND status=1 AND stock>=?",i.getQuantity(),i.getProductId(),i.getQuantity())) {
                        if(i.getQuantity()<1 || p.executeUpdate()!=1) throw new IllegalArgumentException("Sản phẩm "+i.getProductName()+" không còn đủ tồn kho. Vui lòng kiểm tra giỏ hàng.");
                    }
                    execute(c,"UPDATE CartItem SET unitPrice=(SELECT price FROM Product WHERE productId=?),productName=(SELECT productName FROM Product WHERE productId=?) WHERE cartItemId=?",i.getProductId(),i.getProductId(),i.getCartItemId());
                }
                execute(c,"UPDATE Cart SET status=1,buyDate=GETDATE(),recipientName=?,phone=?,address=?,paymentMethod='COD' WHERE cartId=? AND userId=? AND status=0",name,phone,address,cartId,userId);
                c.commit(); return cartId;
            } catch(SQLException|RuntimeException e) { c.rollback(); throw e; }
        }
    }
    public List<Cart_24162141> orders(int userId,Integer status) throws SQLException {
        List<Cart_24162141> list=new ArrayList<>();
        try(Connection c=DBConnection_24162141.open()) {
            String sql="SELECT * FROM Cart WHERE userId=? AND status BETWEEN 1 AND 8"+(status==null?"":" AND status=?")+" ORDER BY buyDate DESC,cartId";
            try(PreparedStatement p=status==null?statement(c,sql,userId):statement(c,sql,userId,status);ResultSet r=p.executeQuery()) { while(r.next()) list.add(map(r)); }
            for(Cart_24162141 o:list) o.setItems(items(c,o.getCartId(),false));
        } return list;
    }
}
