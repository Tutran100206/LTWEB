package vn.iotstar.controller;

import vn.iotstar.util.Constant;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        String cookiePath = req.getContextPath().isEmpty() ? "/" : req.getContextPath();
        Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath(cookiePath);
        resp.addCookie(cookie);

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
