package System;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class MainMenu {
    public static void showMainMenu() {
        JFrame menuFrame = new JFrame("Budget Management System");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(450, 260);
        menuFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel titleLabel = new JLabel("Welcome User");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton addIncomeButton = new JButton("Add Income");
        JButton addExpenseButton = new JButton("Add Expense");
        JButton generateReportsButton = new JButton("Generate Reports");
        JButton logoutButton = new JButton("Logout");

        Font mfont = new Font("Georgia",Font.BOLD,18);
        addIncomeButton.setFont(mfont);
        addExpenseButton.setFont(mfont);
        generateReportsButton.setFont(mfont);
        logoutButton.setFont(mfont);
        addIncomeButton.setFocusPainted(false);

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(addIncomeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(generateReportsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        addIncomeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addExpenseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateReportsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        addIncomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String month = JOptionPane.showInputDialog(menuFrame, "Enter transaction month (e.g., Jan):");
                if (month != null) {
                    String source = JOptionPane.showInputDialog(menuFrame, "Enter income source:");
                    if (source != null) {
                        try {
                            double amount = Double.parseDouble(JOptionPane.showInputDialog(menuFrame, "Enter income amount:"));
                            Transaction.addTransaction("income", source, amount, month);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(menuFrame, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });


        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String month = JOptionPane.showInputDialog(menuFrame, "Enter transaction month (e.g., Jan):");
                if (month != null) {
                    String source = JOptionPane.showInputDialog(menuFrame, "Enter expense source:");
                    if (source != null) {
                        try {
                            double amount = Double.parseDouble(JOptionPane.showInputDialog(menuFrame, "Enter expense amount:"));
                            Transaction.addTransaction("expense", source, amount, month);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(menuFrame, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        generateReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Reports.openGenerateReportsSection();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform logout actions
                Logout.logout();
                menuFrame.dispose();
            }
        });

        mainPanel.add(new JLabel());
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel);
        menuFrame.add(mainPanel);
        menuFrame.setVisible(true);
    }
}
