package vn.edu.de06.controller;
import vn.edu.de06.model.Category_24162141;
import vn.edu.de06.service.CategoryService_24162141;
import vn.edu.de06.util.Validation_24162141;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
@WebServlet(urlPatterns={"/admin/categories","/admin/categories/add","/admin/categories/edit","/admin/categories/delete"})
public class CategoryController_24162141 extends BaseController_24162141 {
    private final CategoryService_24162141 service=new CategoryService_24162141();
    protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String path=req.getServletPath();
        try {
            if(path.endsWith("/delete")) { res.sendError(405); return; }
            if(path.endsWith("/add")) { view(req,res,"admin/category/add"); return; }
            if(path.endsWith("/edit")) {
                Category_24162141 v=service.findById(Validation_24162141.id(req.getParameter("id")));
                if(v==null) { res.sendError(404); return; } req.setAttribute("item",v); view(req,res,"admin/category/edit"); return;
            }
            req.setAttribute("items",service.findAll(page(req,service.count()),5)); view(req,res,"admin/category/list");
        } catch(IllegalArgumentException e) { res.sendError(400); }
        catch(SQLException e) { req.setAttribute("error",sqlError(e)); res.setStatus(503); view(req,res,"error"); }
    }
    protected void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String path=req.getServletPath();
        if(path.equals("/admin/categories")) { res.sendError(405); return; }
        try {
            if(path.endsWith("/delete")) service.delete(Validation_24162141.id(req.getParameter("id")));
            else {
                Category_24162141 v=new Category_24162141(); req.setAttribute("item",v);
                v.setCategoryId(path.endsWith("/add")?0:Validation_24162141.id(req.getParameter("id")));
                v.setCategoryName(req.getParameter("categoryName")); v.setImages(req.getParameter("images")); v.setStatus(Validation_24162141.status(req.getParameter("status"))); service.save(v);
            }
            req.getSession().setAttribute("flash","Đã cập nhật dữ liệu danh mục."); redirect(req,res,"/admin/categories");
        } catch(IllegalArgumentException | SQLException e) {
            String error=e instanceof SQLException?sqlError((SQLException)e):e.getMessage();
            if(path.endsWith("/delete")) { req.getSession().setAttribute("flash",error); redirect(req,res,"/admin/categories"); }
            else { req.setAttribute("error",error); view(req,res,"admin/category/"+(path.endsWith("/add")?"add":"edit")); }
        }
    }
}
