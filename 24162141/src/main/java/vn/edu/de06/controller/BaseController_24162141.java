package vn.edu.de06.controller;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
public abstract class BaseController_24162141 extends HttpServlet {
    protected void view(HttpServletRequest req,HttpServletResponse res,String path) throws ServletException,IOException {
        req.getRequestDispatcher("/WEB-INF/views/"+path+".jsp").forward(req,res);
    }
    protected void redirect(HttpServletRequest req,HttpServletResponse res,String path) throws IOException { res.sendRedirect(req.getContextPath()+path); }
    protected int page(HttpServletRequest req,int total) {
        int p=1; try { p=Integer.parseInt(req.getParameter("page")); } catch(NumberFormatException ignored) {}
        int pages=Math.max(1,(total+4)/5); p=Math.max(1,Math.min(p,pages));
        req.setAttribute("page",p); req.setAttribute("pages",pages); req.setAttribute("total",total); return p;
    }
    protected String sqlError(SQLException e) {
        if(e.getErrorCode()==2601 || e.getErrorCode()==2627) return "Tên đăng nhập hoặc email đã được sử dụng.";
        if(e.getErrorCode()==547) return "Không thể thực hiện: bản ghi đang được tham chiếu hoặc dữ liệu liên kết không hợp lệ.";
        getServletContext().log("Database operation failed",e);
        return "Không thể truy cập dữ liệu. Vui lòng kiểm tra cấu hình SQL Server hoặc thử lại sau.";
    }
}
