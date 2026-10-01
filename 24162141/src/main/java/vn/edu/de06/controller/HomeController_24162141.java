package vn.edu.de06.controller;
import vn.edu.de06.model.User_24162141;
import vn.edu.de06.service.ProductService_24162141;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
@WebServlet(urlPatterns={"/home","/seller/home"})
public class HomeController_24162141 extends BaseController_24162141 {
    protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        User_24162141 u=(User_24162141)req.getSession().getAttribute("account");
        if("/seller/home".equals(req.getServletPath())) {
            if(u==null) { redirect(req,res,"/login"); return; }
            if(u.getSellerId()==null) { redirect(req,res,"/home"); return; }
            try { req.setAttribute("sellerProducts",new ProductService_24162141().groupedBySeller().get(u.getSellerId())); }
            catch(SQLException e) { req.setAttribute("error",sqlError(e)); }
        }
        view(req,res,"home");
    }
}
