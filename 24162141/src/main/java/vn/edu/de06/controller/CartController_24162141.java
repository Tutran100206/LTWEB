package vn.edu.de06.controller;

import vn.edu.de06.model.*;
import vn.edu.de06.service.CartService_24162141;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns={"/cart","/cart/add","/cart/update","/cart/remove","/cart/clear","/checkout","/orders"})
public class CartController_24162141 extends BaseController_24162141 {
    private final CartService_24162141 service=new CartService_24162141();
    private User_24162141 buyer(HttpServletRequest req,HttpServletResponse res) throws IOException {
        User_24162141 u=(User_24162141)req.getSession().getAttribute("account");
        if(u==null) { redirect(req,res,"/login"); return null; }
        if(!"ROLE_USER".equals(u.getRoleName())) { res.sendError(403); return null; } return u;
    }
    protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        User_24162141 u=buyer(req,res); if(u==null) return;
        String path=req.getServletPath();
        if(!"/cart".equals(path) && !"/checkout".equals(path) && !"/orders".equals(path)) { res.sendError(405); return; }
        try {
            if("/orders".equals(path)) {
                Integer status=CartService_24162141.status(req.getParameter("status"));
                req.setAttribute("selectedStatus",status); req.setAttribute("statuses",OrderStatus_24162141.values());
                req.setAttribute("orders",service.orders(u.getUserId(),status)); view(req,res,"cart/orders");
            } else {
                req.setAttribute("cart",service.cart(u.getUserId())); view(req,res,"cart"+path);
            }
        } catch(IllegalArgumentException e) { res.sendError(400,e.getMessage()); }
        catch(SQLException e) { req.setAttribute("error",sqlError(e)); res.setStatus(503); view(req,res,"error"); }
    }
    protected void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        User_24162141 u=buyer(req,res); if(u==null) return;
        String path=req.getServletPath(); boolean checkout="/checkout".equals(path);
        if(!checkout && !path.matches("/cart/(add|update|remove|clear)")) { res.sendError(405); return; }
        try {
            if(checkout) {
                String id=service.checkout(u.getUserId(),req.getParameter("cartId"),req.getParameter("recipientName"),req.getParameter("phone"),req.getParameter("address"));
                req.getSession().setAttribute("flash","Đặt hàng COD thành công. Mã đơn: "+id+". Thanh toán khi nhận hàng."); redirect(req,res,"/orders");
            } else {
                service.change(u.getUserId(),path.substring(6),req.getParameter("productId"),req.getParameter("quantity"));
                req.getSession().setAttribute("flash","Đã cập nhật giỏ hàng."); redirect(req,res,"/cart");
            }
        } catch(IllegalArgumentException e) { showError(req,res,u,checkout,e.getMessage()); }
        catch(SQLException e) { showError(req,res,u,checkout,sqlError(e)); }
    }
    private void showError(HttpServletRequest req,HttpServletResponse res,User_24162141 u,boolean checkout,String error) throws ServletException,IOException {
        req.setAttribute("error",error);
        try { req.setAttribute("cart",service.cart(u.getUserId())); view(req,res,checkout?"cart/checkout":"cart/cart"); }
        catch(SQLException e) { req.setAttribute("error",sqlError(e)); res.setStatus(503); view(req,res,"error"); }
    }
}
