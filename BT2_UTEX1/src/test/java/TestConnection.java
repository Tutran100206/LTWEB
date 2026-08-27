import vn.iotstar.config.DBConnection;

public class TestConnection {

    public static void main(String[] args) {

        try {
            var conn = DBConnection.getConnection();

            System.out.println("KET NOI SQL SERVER THANH CONG!");

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}