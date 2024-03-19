package Management;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Account {
    public static void account() {
        JFrame aframe = new JFrame("Create your Account");
        aframe.setSize(500, 250);
        aframe.setLocationRelativeTo(null);
        aframe.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        JLabel titleLabel = new JLabel("CREATE YOUR ACCOUNT");
        Font titleFont = new Font("Georgia", Font.BOLD, 20);
        titleLabel.setFont(titleFont);
        titlePanel.add(titleLabel);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField usernameField = new JTextField();
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setPreferredSize(new Dimension(150, 30));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setHorizontalAlignment(JPasswordField.CENTER);
        passwordField.setPreferredSize(new Dimension(150, 30));
        JButton createButton = new JButton("Create Account");
        createButton.setPreferredSize(new Dimension(100, 30));
        createButton.setFocusPainted(false);

        Font afont = new Font("Georgia", Font.BOLD, 18);
        usernameLabel.setFont(afont);
        passwordLabel.setFont(afont);
        usernameField.setFont(afont);
        usernameLabel.setFont(afont);
        createButton.setFont(afont);

        // Add components to form panel
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel()); // Spacer
        formPanel.add(createButton);

        // Add panels to frame
        aframe.add(titlePanel, BorderLayout.NORTH);
        aframe.add(formPanel, BorderLayout.CENTER);

        // Action listener for create account button
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate username and password (e.g., check for empty fields)
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(aframe, "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert account details into the database
                try {
                    // Establish database connection
                    Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "c##mb", "sql");

                    // Create a PreparedStatement for inserting account details
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user_accounts (username, password) VALUES (?, ?)");
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    // Execute the PreparedStatement
                    int rowsInserted = preparedStatement.executeUpdate();

                    // Check if account was successfully created
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(aframe, "Account created successfully!");
                    } else {
                        JOptionPane.showMessageDialog(aframe, "Failed to create account", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Close the PreparedStatement and database connection
                    preparedStatement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(aframe, "An error occurred while creating account", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Make the frame visible
        aframe.setVisible(true);
    }
}


