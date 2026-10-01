package vn.edu.de06.service;

import vn.edu.de06.dao.*;
import vn.edu.de06.dao.impl.*;
import vn.edu.de06.model.*;
import vn.edu.de06.util.*;
import java.sql.SQLException;
import java.util.List;

public class UserService_24162141 {
    private final UserDAO_24162141 dao = new UserDAOImpl_24162141();
    private final LookupDAO_24162141 lookup = new LookupDAOImpl_24162141();
    public List<User_24162141> findAll(int page,int size) throws SQLException { return dao.findAll(page,size); }
    public int count() throws SQLException { return dao.count(); }
    public User_24162141 findById(int id) throws SQLException { return dao.findById(id); }
    public List<UserRole_24162141> roles() throws SQLException { return lookup.roles(); }
    public List<Seller_24162141> sellers() throws SQLException { return lookup.sellers(); }
    public void validate(User_24162141 u) throws SQLException {
        u.setUsername(Validation_24162141.text(u.getUsername(),"Tên đăng nhập",50,true));
        u.setEmail(Validation_24162141.email(u.getEmail()));
        u.setFullname(Validation_24162141.text(u.getFullname(),"Họ tên",50,true));
        u.setPhone(Validation_24162141.text(u.getPhone(),"Điện thoại",20,false));
        u.setImages(Validation_24162141.image(u.getImages()));
        if(dao.exists(u.getUsername(),u.getEmail(),u.getUserId())) throw new IllegalArgumentException("Tên đăng nhập hoặc email đã được sử dụng.");
    }
    public void save(User_24162141 u,String password,int actorId) throws SQLException {
        validate(u);
        UserRole_24162141 role=roles().stream().filter(r->r.getRoleId()==u.getRoleId()).findFirst().orElseThrow(()->new IllegalArgumentException("Vai trò không tồn tại."));
        if(u.getSellerId()!=null && sellers().stream().noneMatch(s->s.getSellerId()==u.getSellerId())) throw new IllegalArgumentException("Cửa hàng không tồn tại.");
        if(u.getUserId()==actorId && (u.getStatus()!=1 || !"ROLE_ADMIN".equals(role.getRoleName()))) throw new IllegalArgumentException("Không thể tự khóa hoặc bỏ quyền quản trị của mình.");
        if(password!=null && !password.isEmpty()) {
            validatePassword(password); u.setPassword(Password_24162141.hash(password));
        } else {
            User_24162141 old=u.getUserId()==0?null:dao.findById(u.getUserId());
            if(old==null) throw new IllegalArgumentException("Mật khẩu bắt buộc khi thêm mới.");
            u.setPassword(old.getPassword());
        }
        u.setCode(null); dao.save(u);
    }
    public static void validatePassword(String s) {
        if(s==null || s.length()<6 || s.length()>128) throw new IllegalArgumentException("Mật khẩu cần từ 6 đến 128 ký tự.");
    }
    public void delete(int id,int actorId) throws SQLException {
        if(id==actorId) throw new IllegalArgumentException("Không thể tự xóa tài khoản đang đăng nhập.");
        if(!dao.delete(id)) throw new IllegalArgumentException("Tài khoản không tồn tại.");
    }
}
