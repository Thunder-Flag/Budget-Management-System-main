package Management;
import java.sql.*;

import static Management.LoginWindow.connection;
public class Logout {
    public static void logout() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
