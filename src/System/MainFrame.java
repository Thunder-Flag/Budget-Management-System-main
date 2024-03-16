package System;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame {
    public static void mainframe() {
        JFrame mainFrame = new JFrame("Budget Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);

        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("background.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        imagePanel.setLayout(null);

        JLabel titleLabel = new JLabel("BUDGET MANAGEMENT SYSTEM");
        Font titleFont = new Font("Georgia", Font.BOLD, 30);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.black);
        titleLabel.setBounds(130, 50, 600, 50);


        // Create the buttons
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(50, 200, 200, 50);
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {}
        });

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(50, 260, 200, 50);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the login window
                LoginWindow.open();
            }
        });

        Font mfont = new Font("Georgia",Font.BOLD,18);
        createAccountButton.setFont(mfont);
        loginButton.setFont(mfont);
        createAccountButton.setFocusPainted(false);

        imagePanel.add(titleLabel);
        imagePanel.add(createAccountButton);
        imagePanel.add(loginButton);

        mainFrame.setContentPane(imagePanel);

        mainFrame.setVisible(true);
    }
}



