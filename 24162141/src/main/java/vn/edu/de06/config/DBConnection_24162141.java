package vn.edu.de06.config;

import java.sql.*;
import java.util.Properties;

public final class DBConnection_24162141 {
    private DBConnection_24162141() {}
    public static Connection open() throws SQLException {
        try { Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); }
        catch (ClassNotFoundException e) { throw new SQLException("Missing SQL Server JDBC driver", e); }
        Properties p = new Properties();
        String username = AppConfig_24162141.get("db.username", "");
        if (!username.isEmpty()) {
            p.setProperty("user", username);
            p.setProperty("password", AppConfig_24162141.get("db.password", ""));
        }
        p.setProperty("loginTimeout", "5");
        return DriverManager.getConnection(AppConfig_24162141.get("db.url", "jdbc:sqlserver://localhost:1433;databaseName=DE06_24162141;encrypt=true;trustServerCertificate=true"), p);
    }
}
