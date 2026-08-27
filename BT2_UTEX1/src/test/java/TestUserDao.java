import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.model.User;

public class TestUserDao {

    public static void main(String[] args) {

        UserDao dao = new UserDaoImpl();

        User user = dao.get("admin");

        if (user != null) {
            System.out.println("TIM THAY USER!");
            System.out.println("Username: " + user.getUserName());
            System.out.println("Fullname: " + user.getFullName());
            System.out.println("Role: " + user.getRoleid());
        } else {
            System.out.println("KHONG TIM THAY USER!");
        }
    }
}