package System;
import java.sql.*;

import static System.LoginWindow.connection;
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
