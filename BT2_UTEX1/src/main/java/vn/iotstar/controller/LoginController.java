package vn.iotstar.controller;

import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            String rememberedUsername = null;
            for (Cookie cookie : cookies) {
                if (Constant.COOKIE_REMEMBER.equals(cookie.getName())) {
                    rememberedUsername = cookie.getValue();
                    break;
                }
            }

            if (rememberedUsername != null && !rememberedUsername.trim().isEmpty()) {
                UserService service = new UserServiceImpl();
                User user = service.get(rememberedUsername);
                if (user != null) {
                    HttpSession newSession = req.getSession(true);
                    newSession.setAttribute(Constant.SESSION_ACCOUNT, user);
                    resp.sendRedirect(req.getContextPath() + "/waiting");
                    return;
                }
            }
        }

        req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String remember = req.getParameter("remember");

        if (isBlank(username) || isBlank(password)) {
            req.setAttribute("alert", "Tài khoản hoặc mật khẩu không được rỗng");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
            return;
        }

        UserService service = new UserServiceImpl();
        User user = service.login(username.trim(), password);
        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute(Constant.SESSION_ACCOUNT, user);

            String cookiePath = req.getContextPath().isEmpty() ? "/" : req.getContextPath();
            if (remember != null) {
                Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, user.getUserName());
                cookie.setHttpOnly(true);
                cookie.setMaxAge(30 * 60);
                cookie.setPath(cookiePath);
                resp.addCookie(cookie);
            } else {
                Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, "");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(0);
                cookie.setPath(cookiePath);
                resp.addCookie(cookie);
            }

            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        req.setAttribute("alert", "Tài khoản hoặc mật khẩu không đúng");
        req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
