import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hms_db2";
    private static final String USER = "root";
    private static final String PASSWORD = "YourPassHere";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}