package System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Savings {

    // JDBC URL, username, and password of the Oracle database
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USERNAME = "c##harsh";
    private static final String PASSWORD = "123";

    public static void savings() {
        // Create a new JFrame for the report generation
        JFrame frame = new JFrame("Generate Report");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        frame.add(panel);

        JLabel startMonthLabel = new JLabel("Start Month:");
        panel.add(startMonthLabel, BorderLayout.WEST);

        JLabel endMonthLabel = new JLabel("End Month:");
        panel.add(endMonthLabel, BorderLayout.EAST);

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        JComboBox<String> startMonthDropdown = new JComboBox<>(months);
        JComboBox<String> endMonthDropdown = new JComboBox<>(months);

        JPanel dropdownPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        dropdownPanel.add(startMonthLabel);
        dropdownPanel.add(startMonthDropdown);
        dropdownPanel.add(endMonthLabel);
        dropdownPanel.add(endMonthDropdown);

        panel.add(dropdownPanel, BorderLayout.NORTH);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startMonth = (String) startMonthDropdown.getSelectedItem();
                String endMonth = (String) endMonthDropdown.getSelectedItem();
                displaySavings(startMonth, endMonth, resultTextArea);

                // Adjust frame size based on text area contents
                frame.pack();
            }
        });
        panel.add(generateButton, BorderLayout.SOUTH);

        // Center the frame on the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

        frame.pack();
        frame.setVisible(true);
    }

    private static void displaySavings(String startMonth, String endMonth, JTextArea resultTextArea) {
        try {
            // Establishing the connection
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            // Query to get the sum of income for the specified period
            String incomeQuery = "SELECT SUM(INC_AMT) AS TOTAL_INCOME FROM BUDGET " +
                    "WHERE TO_CHAR(TO_DATE(MONTH, 'Month'), 'MM') BETWEEN " +
                    "TO_CHAR(TO_DATE(?, 'Month'), 'MM') AND TO_CHAR(TO_DATE(?, 'Month'), 'MM')";
            PreparedStatement incomeStatement = connection.prepareStatement(incomeQuery);
            incomeStatement.setString(1, startMonth);
            incomeStatement.setString(2, endMonth);
            ResultSet incomeResult = incomeStatement.executeQuery();

            double totalIncome = 0;

            if (incomeResult.next()) {
                totalIncome = incomeResult.getDouble("TOTAL_INCOME");
            }

            // Query to get the sum of expenses for the specified period
            String expenseQuery = "SELECT SUM(EXP_AMT) AS TOTAL_EXPENSE FROM BUDGET " +
                    "WHERE TO_CHAR(TO_DATE(MONTH, 'Month'), 'MM') BETWEEN " +
                    "TO_CHAR(TO_DATE(?, 'Month'), 'MM') AND TO_CHAR(TO_DATE(?, 'Month'), 'MM')";
            PreparedStatement expenseStatement = connection.prepareStatement(expenseQuery);
            expenseStatement.setString(1, startMonth);
            expenseStatement.setString(2, endMonth);
            ResultSet expenseResult = expenseStatement.executeQuery();

            double totalExpense = 0;

            if (expenseResult.next()) {
                totalExpense = expenseResult.getDouble("TOTAL_EXPENSE");
            }

            // Calculating net savings
            double netSavings = totalIncome - totalExpense;

            // Displaying the result in tabular form
            String result = "For the period from " + startMonth + " to " + endMonth + ":\n";
            result += "+----------------------+----------------------+\n";
            result += "| Total Income         | INR " + String.format("%,.2f", totalIncome) + " |\n";
            result += "| Total Expense        | INR " + String.format("%,.2f", totalExpense) + " |\n";
            result += "| Net Savings          | INR " + String.format("%,.2f", netSavings) + " |\n";
            result += "+----------------------+----------------------+\n";

            // Displaying the result in JTextArea
            resultTextArea.setText(result);

            // Closing resources
            incomeResult.close();
            incomeStatement.close();
            expenseResult.close();
            expenseStatement.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
