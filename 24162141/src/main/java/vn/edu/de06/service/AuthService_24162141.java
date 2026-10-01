package vn.edu.de06.service;
import vn.edu.de06.dao.*;
import vn.edu.de06.dao.impl.*;
import vn.edu.de06.model.*;
import vn.edu.de06.util.*;
import java.sql.SQLException;
import java.security.SecureRandom;
import jakarta.mail.MessagingException;
public class AuthService_24162141 {
    private final UserDAO_24162141 dao=new UserDAOImpl_24162141();
    public User_24162141 login(String username,String password) throws SQLException {
        username=Validation_24162141.text(username,"Tên đăng nhập",50,true);
        if(password==null || password.isEmpty() || password.length()>128) throw new IllegalArgumentException("Vui lòng nhập mật khẩu hợp lệ.");
        User_24162141 u=dao.findByUsername(username);
        if(u==null || !Password_24162141.matches(password,u.getPassword())) throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng.");
        if(u.getStatus()!=1) throw new IllegalArgumentException("Tài khoản chưa kích hoạt hoặc đã bị khóa.");
        u.setPassword(null); u.setCode(null); return u;
    }
    public PendingRegistration_24162141 register(User_24162141 user,String password) throws SQLException,MessagingException {
        new UserService_24162141().validate(user); UserService_24162141.validatePassword(password);
        user.setRoleId(new LookupDAOImpl_24162141().roles().stream().filter(r->"ROLE_USER".equals(r.getRoleName())).findFirst().orElseThrow(()->new IllegalArgumentException("Chưa có vai trò ROLE_USER trong database.")).getRoleId());
        user.setSellerId(null); user.setStatus(0); user.setPassword(Password_24162141.hash(password));
        String otp=String.format("%06d",new SecureRandom().nextInt(1000000));
        new MailService_24162141().sendOtp(user.getEmail(),otp);
        return new PendingRegistration_24162141(user,otp,System.currentTimeMillis()+300000);
    }
    public void activate(PendingRegistration_24162141 pending,String otp) throws SQLException {
        if(pending==null) throw new IllegalArgumentException("Không có đăng ký chờ xác nhận. Vui lòng đăng ký trước.");
        synchronized(pending) {
            pending.verify(otp,System.currentTimeMillis());
            User_24162141 u=pending.getUser();
            if(u.getUserId()!=0) throw new IllegalArgumentException("Tài khoản đã được kích hoạt.");
            new UserService_24162141().validate(u); u.setStatus(1); u.setCode(null); dao.save(u);
        }
    }
}
