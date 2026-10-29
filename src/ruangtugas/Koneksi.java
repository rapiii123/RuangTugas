package ruangtugas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    public static Connection getConnection()
            throws SQLException {

        String url =
            "jdbc:mysql://localhost:3306/ruangtugas";
        String user = "root";
        String password = "";

        return DriverManager.getConnection(
            url, user, password
        );
    }
}
