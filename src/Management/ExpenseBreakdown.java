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
        frame.setSize(460, 450);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        frame.add(panel);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        JComboBox<String> monthDropdown = new JComboBox<>(getMonthsArray());
        monthDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        JComboBox<Integer> yearDropdown = new JComboBox<>();
        for (int year = 2000; year <= 2100; year++) {
            yearDropdown.addItem(year);
        }
        yearDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));

        JLabel expenseSourceLabel = new JLabel("Expense Source:");
        expenseSourceLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        JComboBox<String> expenseSourceDropdown = new JComboBox<>();
        expenseSourceDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));

        inputPanel.add(monthLabel);
        inputPanel.add(monthDropdown);
        inputPanel.add(yearLabel);
        inputPanel.add(yearDropdown);
        inputPanel.add(expenseSourceLabel);
        inputPanel.add(expenseSourceDropdown);

        panel.add(inputPanel, BorderLayout.NORTH);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Georgia", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate");
        generateButton.setPreferredSize(new Dimension(150, 50));
        generateButton.setFont(new Font("Georgia", Font.BOLD, 18));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedMonth = (String) monthDropdown.getSelectedItem();
                int selectedYear = (int) yearDropdown.getSelectedItem();
                String selectedExpenseSource = (String) expenseSourceDropdown.getSelectedItem();

                if (selectedExpenseSource == null) {
                    selectedExpenseSource = "";
                }

                displayExpenseBreakdown(selectedMonth, selectedYear, selectedExpenseSource, resultTextArea);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(generateButton);
        buttonPanel.add(Box.createHorizontalGlue());
        frame.add(buttonPanel, BorderLayout.SOUTH);

        populateExpenseSourceDropdown(expenseSourceDropdown);

        frame.setVisible(true);
    }

    private static String[] getMonthsArray() {
        return new String[]{"All", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
    }

    private static void populateExpenseSourceDropdown(JComboBox<String> dropdown) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String query = "SELECT DISTINCT EXP_SRC FROM BUDGET";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String expenseSource = resultSet.getString("EXP_SRC");
                dropdown.addItem(expenseSource);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void displayExpenseBreakdown(String selectedMonth, int selectedYear, String selectedExpenseSource, JTextArea resultTextArea) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String query = "SELECT SUM(EXP_AMT) AS TOTAL_EXPENSE, MONTH, EXP_SRC FROM BUDGET " +
                    "WHERE YEAR = ? ";
            if (!selectedMonth.equalsIgnoreCase("All")) {
                query += "AND MONTH = ? ";
            }
            if (!selectedExpenseSource.isEmpty()) {
                query += "AND EXP_SRC = ? ";
            }
            query += "GROUP BY MONTH, EXP_SRC ORDER BY MONTH";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedYear);
            int parameterIndex = 2;
            if (!selectedMonth.equalsIgnoreCase("All")) {
                statement.setString(parameterIndex++, selectedMonth);
            }
            if (!selectedExpenseSource.isEmpty()) {
                statement.setString(parameterIndex, selectedExpenseSource);
            }

            ResultSet result = statement.executeQuery();

            StringBuilder resultText = new StringBuilder();
            resultText.append("Expense Breakdown for ");
            if (!selectedMonth.equalsIgnoreCase("All")) {
                resultText.append(selectedMonth).append(" ");
            }
            resultText.append(selectedYear).append(":\n");
            resultText.append("+----------------------+----------------------+----------------------+\n");
            resultText.append("| Month                | Expense Source       | Total Expense        |\n");

            double totalExpenseYear = 0; // Initialize total expense for the year
            while (result.next()) {
                String month = result.getString("MONTH");
                String expenseSource = result.getString("EXP_SRC");
                Double totalExpense = result.getDouble("TOTAL_EXPENSE");

                if (expenseSource != null && totalExpense != null) {
                    resultText.append(String.format("| %-20s | %-20s | INR %-16.2f |\n", month, expenseSource, totalExpense));
                    totalExpenseYear += totalExpense; // Accumulate total expense for the year
                }
            }

            resultText.append("+----------------------+----------------------+----------------------+\n");
            resultText.append("Total Expense for the Year: INR ").append(String.format("%.2f", totalExpenseYear)).append("\n");

            resultTextArea.setText(resultText.toString());

            result.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}