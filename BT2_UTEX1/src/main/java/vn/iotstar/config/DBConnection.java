package vn.iotstar.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static final String SERVER = "localhost";
    public static final String INSTANCE = "SQLEXPRESS";
    public static final String DATABASE = "BT2_UTEX1";

    public static final String USERNAME = "bt2_user";
    public static final String PASSWORD = "123456";

    private static final String URL =
            "jdbc:sqlserver://" + SERVER +
                    ";instanceName=" + INSTANCE +
                    ";databaseName=" + DATABASE +
                    ";encrypt=true" +
                    ";trustServerCertificate=true";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}