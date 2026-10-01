package vn.edu.de06.filter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
public class RequestFilter_24162141 implements Filter {
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) throws IOException,ServletException {
        HttpServletRequest req=(HttpServletRequest)request; HttpServletResponse res=(HttpServletResponse)response;
        req.setCharacterEncoding("UTF-8"); res.setCharacterEncoding("UTF-8");
        res.setHeader("X-Content-Type-Options","nosniff"); res.setHeader("X-Frame-Options","SAMEORIGIN");
        if(req.getServletPath().startsWith("/assets/")) { chain.doFilter(req,res); return; }
        res.setHeader("Cache-Control","no-store");
        HttpSession session=req.getSession();
        if(session.getAttribute("csrf")==null) session.setAttribute("csrf",UUID.randomUUID().toString());
        if("POST".equals(req.getMethod()) && !session.getAttribute("csrf").equals(req.getParameter("csrf"))) { res.sendError(403,"Phiên biểu mẫu không hợp lệ. Hãy tải lại trang."); return; }
        Object flash=session.getAttribute("flash");
        if(flash!=null) { req.setAttribute("message",flash); session.removeAttribute("flash"); }
        chain.doFilter(req,res);
    }
}
