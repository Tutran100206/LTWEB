package vn.edu.de06.filter;
import vn.edu.de06.model.User_24162141;
import vn.edu.de06.service.UserService_24162141;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
public class AdminFilter_24162141 implements Filter {
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) throws IOException,ServletException {
        HttpServletRequest req=(HttpServletRequest)request; HttpServletResponse res=(HttpServletResponse)response;
        HttpSession session=req.getSession(false);
        User_24162141 account=session==null?null:(User_24162141)session.getAttribute("account");
        if(account==null) { res.sendRedirect(req.getContextPath()+"/login"); return; }
        try {
            User_24162141 current=new UserService_24162141().findById(account.getUserId());
            if(current==null || current.getStatus()!=1 || !current.isAdmin()) { res.sendError(403); return; }
            current.setPassword(null); current.setCode(null); session.setAttribute("account",current);
            chain.doFilter(req,res);
        } catch(SQLException e) { req.getServletContext().log("Admin authorization database failure",e); res.sendError(503); }
    }
}
