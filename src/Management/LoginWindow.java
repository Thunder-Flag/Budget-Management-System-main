package Management;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
public class LoginWindow {
    public static Connection connection;

    public static void open() {
        JFrame loginFrame = new JFrame("Login to your account");
        loginFrame.setSize(460, 250);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new BorderLayout());

        // Panel for the title
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        JLabel titleLabel = new JLabel("BUDGET MANAGEMENT SYSTEM");
        Font titleFont = new Font("Georgia", Font.BOLD, 20);
        titleLabel.setFont(titleFont);
        titlePanel.add(titleLabel);

        // Panel for the login form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        // Components for the login form
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField usernameField = new JTextField();
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setPreferredSize(new Dimension(150, 30));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setHorizontalAlignment(JPasswordField.CENTER);
        passwordField.setPreferredSize(new Dimension(150, 30));
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setFocusPainted(false);

        Font lfont = new Font("Georgia", Font.BOLD, 18);
        usernameLabel.setFont(lfont);
        passwordLabel.setFont(lfont);
        usernameField.setFont(lfont);
        usernameLabel.setFont(lfont);
        loginButton.setFont(lfont);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean authenticated = authenticate(username, password);
                if (authenticated) {
                    loginFrame.dispose(); // Close the login window
                    MainMenu.showMainMenu();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel());
        formPanel.add(loginButton);

        loginFrame.add(titlePanel, BorderLayout.NORTH);
        loginFrame.add(formPanel, BorderLayout.CENTER);

        loginFrame.setVisible(true);
    }

    private static boolean authenticate(String username, String password) {
        try {
            // Establish database connection
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:orcl", "c##mb", "sql");

            // Create a PreparedStatement to query the database for the provided username and password
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM user_accounts WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if the query returned any rows
            if (resultSet.next()) {
                // Close the ResultSet, PreparedStatement, and database connection
                resultSet.close();
                preparedStatement.close();
                return true; // Username and password match, authentication successful
            } else {
                // Close the ResultSet, PreparedStatement, and database connection
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return false; // Username and/or password do not match
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred during authentication
        }
    }
}

