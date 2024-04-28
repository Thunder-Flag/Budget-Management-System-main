package Management;
import java.sql.*;
public class DBConnection {
    static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    static final String USER = "c##mb";
    static final String PASS = "sql";
    public static Connection connection;
    public static Connection connection(){
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL,USER,PASS);
            return connection;
        }catch (SQLException | ClassNotFoundException se){
            se.printStackTrace();
        }

        return null;
    }
}
