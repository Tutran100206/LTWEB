package vn.edu.de06.dao;
import vn.edu.de06.model.User_24162141;
import java.sql.SQLException;
import java.util.List;
public interface UserDAO_24162141 {
    List<User_24162141> findAll(int page, int pageSize) throws SQLException;
    int count() throws SQLException;
    User_24162141 findById(int id) throws SQLException;
    void save(User_24162141 value) throws SQLException;
    boolean delete(int id) throws SQLException;
    User_24162141 findByUsername(String username) throws SQLException;
    boolean exists(String username, String email, int excludeId) throws SQLException;
}
