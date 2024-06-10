package Management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Savings {
    public static void savings() {
        // Create a new JFrame for the report generation
        JFrame frame = new JFrame("Calculate Savings");
        frame.setSize(420, 450);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout with Y_AXIS

        // First drop-down panel: Start Month
        JPanel startMonthPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        startMonthPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10)); // Add padding
        JLabel startMonthLabel = new JLabel("Start Month:");
        startMonthLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        startMonthPanel.add(startMonthLabel);
        // Add start month dropdown
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        JComboBox<String> startMonthDropdown = new JComboBox<>(months);
        startMonthDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));
        startMonthPanel.add(startMonthDropdown);
        panel.add(startMonthPanel);

        // Second drop-down panel: End Month
        JPanel endMonthPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        endMonthPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10)); // Add padding
        JLabel endMonthLabel = new JLabel("End Month:");
        endMonthLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        endMonthPanel.add(endMonthLabel);
        // Add end month dropdown
        JComboBox<String> endMonthDropdown = new JComboBox<>(months);
        endMonthDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));
        endMonthPanel.add(endMonthDropdown);
        panel.add(endMonthPanel);

        // Third drop-down panel: Year
        JPanel yearPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        yearPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10)); // Add padding
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        yearPanel.add(yearLabel);
        // Add year dropdown
        JComboBox<Integer> yearDropdown = new JComboBox<>();
        for (int year = 2000; year <= 2100; year++) {
            yearDropdown.addItem(year);
        }
        yearDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));
        yearPanel.add(yearDropdown);
        panel.add(yearPanel);

        // Add the panel to the frame
        frame.add(panel, BorderLayout.NORTH);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Georgia", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate");
        generateButton.setPreferredSize(new Dimension(150, 50)); // Set preferred size
        generateButton.setFont(new Font("Georgia", Font.BOLD, 18));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startMonth = (String) startMonthDropdown.getSelectedItem();
                String endMonth = (String) endMonthDropdown.getSelectedItem();
                int year = (int) yearDropdown.getSelectedItem();
                displaySavings(startMonth, endMonth, year, resultTextArea);

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
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Set the initial size of the frame
        frame.setVisible(true);
    }

    private static void displaySavings(String startMonth, String endMonth, int year, JTextArea resultTextArea) {
        try {
            // Establishing the connection
            Connection connection = DBConnection.connection();

            // Query to get the sum of income for the specified period
            String incomeQuery = "SELECT SUM(INC_AMT) AS TOTAL_INCOME FROM BUDGET " +
                    "WHERE TO_CHAR(TO_DATE(MONTH, 'Month'), 'MM') BETWEEN " +
                    "TO_CHAR(TO_DATE(?, 'Month'), 'MM') AND TO_CHAR(TO_DATE(?, 'Month'), 'MM')" + "AND YEAR = ?";
            PreparedStatement incomeStatement = connection.prepareStatement(incomeQuery);
            incomeStatement.setString(1, startMonth);
            incomeStatement.setString(2, endMonth);
            incomeStatement.setInt(3, year);
            ResultSet incomeResult = incomeStatement.executeQuery();

            double totalIncome = 0;

            if (incomeResult.next()) {
                totalIncome = incomeResult.getDouble("TOTAL_INCOME");
            }

            // Query to get the sum of expenses for the specified period
            String expenseQuery = "SELECT SUM(EXP_AMT) AS TOTAL_EXPENSE FROM BUDGET " +
                    "WHERE TO_CHAR(TO_DATE(MONTH, 'Month'), 'MM') BETWEEN " +
                    "TO_CHAR(TO_DATE(?, 'Month'), 'MM') AND TO_CHAR(TO_DATE(?, 'Month'), 'MM')" + "AND YEAR = ?";
            PreparedStatement expenseStatement = connection.prepareStatement(expenseQuery);
            expenseStatement.setString(1, startMonth);
            expenseStatement.setString(2, endMonth);
            expenseStatement.setInt(3, year);
            ResultSet expenseResult = expenseStatement.executeQuery();

            double totalExpense = 0;

            if (expenseResult.next()) {
                totalExpense = expenseResult.getDouble("TOTAL_EXPENSE");
            }

            // Calculating net savings
            double netSavings = totalIncome - totalExpense;

            // Displaying the result in tabular form
            String result = "For the period from " + startMonth + " to " + endMonth + " in " + year + ":\n";
            result += "+----------------------+----------------------+\n";
            result += "| Total Income         | INR " + String.format("%,.2f", totalIncome) + " |\n";
            result += "| Total Expense        | INR " + String.format("%,.2f", totalExpense) + " |\n";
            result += "| Net Savings          | INR " + String.format("%,.2f", netSavings) + " |\n";
            result += "+----------------------+----------------------+\n";

            // Displaying the result in JTextArea
            resultTextArea.setLineWrap(true); // Enable line wrapping
            resultTextArea.setWrapStyleWord(true); // Wrap at word boundaries
            resultTextArea.setText(result);

            // Adjusting JTextArea preferred size based on content
            resultTextArea.setRows(resultTextArea.getLineCount());


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
