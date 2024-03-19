package Management;

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
        frame.setSize(460,450);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        frame.add(panel);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel startMonthLabel = new JLabel("Start Month:");
        startMonthLabel.setFont(new Font("Georgia",Font.PLAIN,16));
        JComboBox<String> startMonthDropdown = new JComboBox<>(getMonthsArray());
        startMonthDropdown.setFont(new Font("Georgia",Font.PLAIN,16));

        JLabel endMonthLabel = new JLabel("End Month:");
        endMonthLabel.setFont(new Font("Georgia",Font.PLAIN,16));
        JComboBox<String> endMonthDropdown = new JComboBox<>(getMonthsArray());
        endMonthDropdown.setFont(new Font("Georgia",Font.PLAIN,16));

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        JComboBox<Integer> yearDropdown = new JComboBox<>();
        for (int year = 2000; year <= 2100; year++) {
            yearDropdown.addItem(year);
        }
        yearDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));

        JLabel expenseSourceLabel = new JLabel("Expense Source:");
        expenseSourceLabel.setFont(new Font("Georgia",Font.PLAIN,16));
        JComboBox<String> expenseSourceDropdown = new JComboBox<>();
        expenseSourceDropdown.setFont(new Font("Georgia",Font.PLAIN,16));
        JTextField customExpenseSourceField = new JTextField();
        customExpenseSourceField.setFont(new Font("Georgia",Font.PLAIN,16));
        customExpenseSourceField.setMaximumSize(new Dimension(200, 25));

        JLabel customexpense = new JLabel("Custom Expense Source:");
        customexpense.setFont(new Font("Georgia",Font.PLAIN,16));

        inputPanel.add(startMonthLabel);
        inputPanel.add(startMonthDropdown);
        inputPanel.add(endMonthLabel);
        inputPanel.add(endMonthDropdown);
        inputPanel.add(yearLabel);
        inputPanel.add(yearDropdown);
        inputPanel.add(expenseSourceLabel);
        inputPanel.add(expenseSourceDropdown);
        inputPanel.add(customexpense);
        inputPanel.add(customExpenseSourceField);

        panel.add(inputPanel, BorderLayout.NORTH);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Georgia", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate");
        generateButton.setPreferredSize(new Dimension(150, 50));
        generateButton.setFont(new Font("Georgia",Font.BOLD,18));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startMonth = (String) startMonthDropdown.getSelectedItem();
                String endMonth = (String) endMonthDropdown.getSelectedItem();
                int year = (int) yearDropdown.getSelectedItem();
                String expenseSource;

                if (!customExpenseSourceField.getText().isEmpty()) {
                    expenseSource = customExpenseSourceField.getText();
                    expenseSourceDropdown.setSelectedIndex(-1); // Reset dropdown selection
                } else {
                    expenseSource = (String) expenseSourceDropdown.getSelectedItem();
                }

                displayExpenseBreakdown(startMonth, endMonth, year, expenseSource, resultTextArea);

            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS)); // Set BoxLayout
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add padding
        buttonPanel.add(Box.createHorizontalGlue()); // Add space before button
        buttonPanel.add(generateButton);
        buttonPanel.add(Box.createHorizontalGlue()); // Add space after button
        frame.add(buttonPanel, BorderLayout.SOUTH);

        populateExpenseSourceDropdown(expenseSourceDropdown);

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

    private static void displayExpenseBreakdown(String startMonth, String endMonth, int year, String expenseSource, JTextArea resultTextArea) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String startMonthNum = getMonthNumber(startMonth);
            String endMonthNum = getMonthNumber(endMonth);

            String query = "SELECT SUM(EXP_AMT) AS TOTAL_EXPENSE FROM BUDGET " +
                    "WHERE TO_CHAR(TO_DATE(MONTH, 'Month'), 'MM') BETWEEN ? AND ? " +
                    "AND YEAR = ? AND EXP_SRC = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, startMonthNum);
            statement.setString(2, endMonthNum);
            statement.setInt(3, year);
            statement.setString(4, expenseSource);
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
