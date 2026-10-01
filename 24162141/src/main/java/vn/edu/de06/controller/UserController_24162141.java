package vn.edu.de06.controller;
import vn.edu.de06.model.User_24162141;
import vn.edu.de06.service.UserService_24162141;
import vn.edu.de06.util.Validation_24162141;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
@WebServlet(urlPatterns={"/admin/users","/admin/users/add","/admin/users/edit","/admin/users/delete"})
public class UserController_24162141 extends BaseController_24162141 {
    private final UserService_24162141 service=new UserService_24162141();
    private void form(HttpServletRequest req,HttpServletResponse res) throws SQLException,ServletException,IOException {
        req.setAttribute("roles",service.roles()); req.setAttribute("sellers",service.sellers());
        view(req,res,"admin/user/"+(req.getServletPath().endsWith("/add")?"add":"edit"));
    }
    protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String path=req.getServletPath();
        try {
            if(path.endsWith("/delete")) { res.sendError(405); return; }
            if(path.endsWith("/add")) { form(req,res); return; }
            if(path.endsWith("/edit")) {
                User_24162141 u=service.findById(Validation_24162141.id(req.getParameter("id")));
                if(u==null) { res.sendError(404); return; } u.setPassword(null); u.setCode(null); req.setAttribute("item",u); form(req,res); return;
            }
            req.setAttribute("items",service.findAll(page(req,service.count()),5)); view(req,res,"admin/user/list");
        } catch(IllegalArgumentException e) { res.sendError(400); }
        catch(SQLException e) { req.setAttribute("error",sqlError(e)); res.setStatus(503); view(req,res,"error"); }
    }
    protected void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String path=req.getServletPath();
        if(path.equals("/admin/users")) { res.sendError(405); return; }
        try {
            int actor=((User_24162141)req.getSession().getAttribute("account")).getUserId();
            if(path.endsWith("/delete")) service.delete(Validation_24162141.id(req.getParameter("id")),actor);
            else {
                User_24162141 u=new User_24162141(); req.setAttribute("item",u);
                u.setUserId(path.endsWith("/add")?0:Validation_24162141.id(req.getParameter("id")));
                u.setUsername(req.getParameter("username")); u.setEmail(req.getParameter("email")); u.setFullname(req.getParameter("fullname")); u.setPhone(req.getParameter("phone")); u.setImages(req.getParameter("images"));
                u.setStatus(Validation_24162141.status(req.getParameter("status"))); u.setRoleId(Validation_24162141.id(req.getParameter("roleId")));
                String seller=req.getParameter("sellerId"); u.setSellerId(seller==null || seller.isBlank()?null:Validation_24162141.id(seller));
                service.save(u,req.getParameter("password"),actor);
            }
            req.getSession().setAttribute("flash","Đã cập nhật dữ liệu người dùng."); redirect(req,res,"/admin/users");
        } catch(IllegalArgumentException | SQLException e) {
            String error=e instanceof SQLException?sqlError((SQLException)e):e.getMessage();
            if(path.endsWith("/delete")) { req.getSession().setAttribute("flash",error); redirect(req,res,"/admin/users"); }
            else { req.setAttribute("error",error); try { form(req,res); } catch(SQLException ex) { req.setAttribute("error",sqlError(ex)); view(req,res,"error"); } }
        }
    }
}
