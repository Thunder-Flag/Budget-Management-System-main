package Management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Savings {

    // JDBC URL, username, and password of the Oracle database
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USERNAME = "c##mb";
    private static final String PASSWORD = "sql";

    public static void savings() {
        // Create a new JFrame for the report generation
        JFrame frame = new JFrame("Calculate Savings");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        frame.add(panel);

        JLabel startMonthLabel = new JLabel("Start Month:");
        startMonthLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        JLabel endMonthLabel = new JLabel("End Month:");
        endMonthLabel.setFont(new Font("Georgia", Font.PLAIN, 16));

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        JComboBox<String> startMonthDropdown = new JComboBox<>(months);
        startMonthDropdown.setFont(new Font("Georgia",Font.PLAIN,14));
        JComboBox<String> endMonthDropdown = new JComboBox<>(months);
        endMonthDropdown.setFont(new Font("Georgia",Font.PLAIN,14));

        JPanel dropdownPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        dropdownPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10)); // Add padding
        dropdownPanel.add(startMonthLabel);
        dropdownPanel.add(startMonthDropdown);
        dropdownPanel.add(endMonthLabel);
        dropdownPanel.add(endMonthDropdown);

        panel.add(dropdownPanel, BorderLayout.NORTH);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Georgia", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate");
        generateButton.setPreferredSize(new Dimension(150, 50)); // Set preferred size
        generateButton.setFont(new Font("Georgia",Font.BOLD,18));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startMonth = (String) startMonthDropdown.getSelectedItem();
                String endMonth = (String) endMonthDropdown.getSelectedItem();
                displaySavings(startMonth, endMonth, resultTextArea);

                // Center the frame on the screen after generating report
                frame.setLocationRelativeTo(null);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS)); // Set BoxLayout
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add padding
        buttonPanel.add(Box.createHorizontalGlue()); // Add space before button
        buttonPanel.add(generateButton);
        buttonPanel.add(Box.createHorizontalGlue()); // Add space after button
        panel.add(buttonPanel, BorderLayout.SOUTH);

         // Set the initial size of the frame
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
