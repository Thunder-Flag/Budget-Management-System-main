package System;
import javax.swing.*;
import java.sql.*;
import static System.LoginWindow.connection;

public class Transaction {
    private static JFrame loginFrame;
    public static void addTransaction(String type, String source, double amount, String month) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO budget (inc_src, inc_amt, exp_src, exp_amt, month) VALUES (?, ?, ?, ?, ?)");
            if (type.equals("income")) {
                statement.setString(1, source);
                statement.setDouble(2, amount);
                statement.setNull(3, Types.VARCHAR);
                statement.setNull(4, Types.DOUBLE);
            } else if (type.equals("expense")) {
                statement.setNull(1, Types.VARCHAR);
                statement.setNull(2, Types.DOUBLE);
                statement.setString(3, source);
                statement.setDouble(4, amount);
            }
            statement.setString(5, month);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(loginFrame, "Transaction added successfully!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
