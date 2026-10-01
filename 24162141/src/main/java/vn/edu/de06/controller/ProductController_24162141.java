package vn.edu.de06.controller;
import vn.edu.de06.model.Product_24162141;
import vn.edu.de06.service.ProductService_24162141;
import vn.edu.de06.util.Validation_24162141;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
@WebServlet(urlPatterns={"/products","/product-detail"})
public class ProductController_24162141 extends BaseController_24162141 {
    private final ProductService_24162141 service=new ProductService_24162141();
    protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        try {
            if("/products".equals(req.getServletPath())) { req.setAttribute("groups",service.groupedBySeller()); view(req,res,"product/products"); }
            else {
                Product_24162141 p=service.findById(Validation_24162141.id(req.getParameter("id")));
                if(p==null) { res.sendError(404); return; } req.setAttribute("product",p); view(req,res,"product/product-detail");
            }
        } catch(IllegalArgumentException e) { res.sendError(400); }
        catch(SQLException e) { req.setAttribute("error",sqlError(e)); res.setStatus(503); view(req,res,"error"); }
    }
}
