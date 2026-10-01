package vn.edu.de06.controller;
import vn.edu.de06.model.*;
import vn.edu.de06.service.AuthService_24162141;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.mail.MessagingException;
@WebServlet(urlPatterns={"/login","/register","/verify-otp","/logout"})
public class AuthController_24162141 extends BaseController_24162141 {
    private final AuthService_24162141 service=new AuthService_24162141();
    protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        if("/logout".equals(req.getServletPath())) { res.sendError(405); return; }
        view(req,res,"auth"+req.getServletPath());
    }
    protected void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        String path=req.getServletPath();
        try {
            if("/logout".equals(path)) { req.getSession().invalidate(); redirect(req,res,"/home"); return; }
            if("/login".equals(path)) {
                User_24162141 u=service.login(req.getParameter("username"),req.getParameter("password"));
                req.changeSessionId(); req.getSession().setAttribute("account",u);
                redirect(req,res,u.isAdmin()?"/admin":u.getSellerId()!=null?"/seller/home":"/home"); return;
            }
            if("/register".equals(path)) {
                Long sent=(Long)req.getSession().getAttribute("otpSentAt");
                if(sent!=null && System.currentTimeMillis()-sent<60000) throw new IllegalArgumentException("Vui lòng chờ 60 giây trước khi yêu cầu OTP mới.");
                User_24162141 u=new User_24162141();
                u.setUsername(req.getParameter("username")); u.setFullname(req.getParameter("fullname")); u.setEmail(req.getParameter("email")); u.setPhone(req.getParameter("phone"));
                PendingRegistration_24162141 pending=service.register(u,req.getParameter("password"));
                req.getSession().setAttribute("pending",pending); req.getSession().setAttribute("otpSentAt",System.currentTimeMillis());
                req.getSession().setAttribute("flash","Đã gửi mã OTP qua email. Mã có hiệu lực 5 phút.");
                redirect(req,res,"/verify-otp"); return;
            }
            service.activate((PendingRegistration_24162141)req.getSession().getAttribute("pending"),req.getParameter("otp"));
            req.getSession().removeAttribute("pending"); req.getSession().setAttribute("flash","Kích hoạt thành công. Bạn có thể đăng nhập."); redirect(req,res,"/login");
        } catch(IllegalArgumentException e) { req.setAttribute("error",e.getMessage()); view(req,res,"auth"+path); }
        catch(SQLException e) { req.setAttribute("error",sqlError(e)); view(req,res,"auth"+path); }
        catch(MessagingException e) { getServletContext().log("OTP mail failed",e); req.setAttribute("error","Chưa gửi được email OTP. Kiểm tra cấu hình SMTP rồi thử lại; tài khoản chưa được tạo."); view(req,res,"auth/register"); }
    }
}
