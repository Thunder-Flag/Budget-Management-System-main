package System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseBreakdown {

    // JDBC URL, username, and password of the Oracle database
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USERNAME = "c##mb";
    private static final String PASSWORD = "sql";

    public static void expenseBreakdown() {
        // Create a new JFrame for the expense breakdown
        JFrame frame = new JFrame("Expense Breakdown");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        frame.add(panel);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel startMonthLabel = new JLabel("Start Month:");
        JComboBox<String> startMonthDropdown = new JComboBox<>(getMonthsArray());

        JLabel endMonthLabel = new JLabel("End Month:");
        JComboBox<String> endMonthDropdown = new JComboBox<>(getMonthsArray());

        JLabel expenseSourceLabel = new JLabel("Expense Source:");
        JComboBox<String> expenseSourceDropdown = new JComboBox<>();
        JTextField customExpenseSourceField = new JTextField();
        customExpenseSourceField.setMaximumSize(new Dimension(200, 25));

        inputPanel.add(startMonthLabel);
        inputPanel.add(startMonthDropdown);
        inputPanel.add(endMonthLabel);
        inputPanel.add(endMonthDropdown);
        inputPanel.add(expenseSourceLabel);
        inputPanel.add(expenseSourceDropdown);
        inputPanel.add(new JLabel("Custom Expense Source:"));
        inputPanel.add(customExpenseSourceField);

        panel.add(inputPanel, BorderLayout.NORTH);

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
                String expenseSource;

                if (!customExpenseSourceField.getText().isEmpty()) {
                    expenseSource = customExpenseSourceField.getText();
                    expenseSourceDropdown.setSelectedIndex(-1); // Reset dropdown selection
                } else {
                    expenseSource = (String) expenseSourceDropdown.getSelectedItem();
                }

                displayExpenseBreakdown(startMonth, endMonth, expenseSource, resultTextArea);

                frame.pack();
            }
        });
        panel.add(generateButton, BorderLayout.SOUTH);

        populateExpenseSourceDropdown(expenseSourceDropdown);

        // Center the frame on the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        frame.pack();
        frame.setVisible(true);
    }

    private static String[] getMonthsArray() {
        return new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
    }

    private static void populateExpenseSourceDropdown(JComboBox<String> dropdown) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String query = "SELECT DISTINCT EXP_SRC FROM BUDGET";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<String> expenseSources = new ArrayList<>();
            while (resultSet.next()) {
                String expenseSource = resultSet.getString("EXP_SRC");
                expenseSources.add(expenseSource);
            }

            for (String source : expenseSources) {
                dropdown.addItem(source);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void displayExpenseBreakdown(String startMonth, String endMonth, String expenseSource, JTextArea resultTextArea) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String startMonthNum = getMonthNumber(startMonth);
            String endMonthNum = getMonthNumber(endMonth);

            String query = "SELECT SUM(EXP_AMT) AS TOTAL_EXPENSE FROM BUDGET " +
                    "WHERE TO_CHAR(TO_DATE(MONTH, 'Month'), 'MM') BETWEEN ? AND ? " +
                    "AND EXP_SRC = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, startMonthNum);
            statement.setString(2, endMonthNum);
            statement.setString(3, expenseSource);
            ResultSet result = statement.executeQuery();

            String resultText = "Expense Breakdown for " + expenseSource +
                    " from " + startMonth + " to " + endMonth + ":\n";
            resultText += "+----------------------+\n";
            resultText += "| Total Expense         |\n";
            resultText += "+----------------------+\n";

            double totalExpense = 0;
            if (result.next()) {
                totalExpense = result.getDouble("TOTAL_EXPENSE");
                resultText += String.format("| INR %-20.2f |\n", totalExpense);
            }

            resultText += "+----------------------+\n";

            resultTextArea.setText(resultText);

            result.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static String getMonthNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "january":
                return "01";
            case "february":
                return "02";
            case "march":
                return "03";
            case "april":
                return "04";
            case "may":
                return "05";
            case "june":
                return "06";
            case "july":
                return "07";
            case "august":
                return "08";
            case "september":
                return "09";
            case "october":
                return "10";
            case "november":
                return "11";
            case "december":
                return "12";
            default:
                throw new IllegalArgumentException("Invalid month name: " + monthName);
        }
    }
}
