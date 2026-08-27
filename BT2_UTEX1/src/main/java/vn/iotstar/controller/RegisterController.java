package vn.iotstar.controller;

import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }
        req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");

        if (isBlank(username) || isBlank(password) || isBlank(email) || isBlank(fullname)) {
            req.setAttribute("alert", "Vui lòng nhập đầy đủ thông tin bắt buộc!");
            req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
            return;
        }

        UserService service = new UserServiceImpl();

        if (service.checkExistEmail(email.trim())) {
            req.setAttribute("alert", "Email đã tồn tại!");
            req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
            return;
        }

        if (service.checkExistUsername(username.trim())) {
            req.setAttribute("alert", "Tài khoản đã tồn tại!");
            req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
            return;
        }

        if (!isBlank(phone) && service.checkExistPhone(phone.trim())) {
            req.setAttribute("alert", "Số điện thoại đã tồn tại!");
            req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
            return;
        }

        boolean isSuccess = service.register(
                username.trim(),
                password,
                email.trim(),
                fullname.trim(),
                phone == null ? null : phone.trim()
        );

        if (isSuccess) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("alert", "System error!");
        req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
