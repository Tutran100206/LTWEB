package vn.edu.de06.controller;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
@WebServlet(urlPatterns={"/admin","/admin/"})
public class AdminController_24162141 extends BaseController_24162141 {
    protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException { view(req,res,"admin/dashboard"); }
}
