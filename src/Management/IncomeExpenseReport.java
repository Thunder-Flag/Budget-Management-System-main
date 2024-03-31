package Management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Calendar;

public class IncomeExpenseReport {

    // JDBC URL, username, and password of the Oracle database
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USERNAME = "c##mb";
    private static final String PASSWORD = "sql";

    public static void GenerateAllReports() {
        // Create a new JFrame for the report generation
        JFrame frame = new JFrame("Generate All Reports");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout with Y_AXIS

        // Month dropdown panel
        JPanel monthPanel = new JPanel();
        monthPanel.setLayout(new BoxLayout(monthPanel, BoxLayout.X_AXIS)); // BoxLayout with X_AXIS
        monthPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        monthPanel.add(monthLabel);
        // Add month dropdown
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthDropdown = new JComboBox<>(months);
        monthDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));
        monthDropdown.setMaximumSize(new Dimension(150, 30)); // Set maximum size
        monthPanel.add(monthDropdown);
        panel.add(monthPanel);

        // Year dropdown panel
        JPanel yearPanel = new JPanel();
        yearPanel.setLayout(new BoxLayout(yearPanel, BoxLayout.X_AXIS)); // BoxLayout with X_AXIS
        yearPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        yearPanel.add(yearLabel);
        // Add year dropdown
        JComboBox<Integer> yearDropdown = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= 2000; year--) {
            yearDropdown.addItem(year);
        }
        yearDropdown.setFont(new Font("Georgia", Font.PLAIN, 16));
        yearDropdown.setMaximumSize(new Dimension(150, 30)); // Set maximum size
        yearPanel.add(yearDropdown);
        panel.add(yearPanel);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Georgia", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        panel.add(scrollPane);

        JButton generateButton = new JButton("Generate");
        generateButton.setPreferredSize(new Dimension(150, 50)); // Set preferred size
        generateButton.setMaximumSize(new Dimension(150, 50)); // Set maximum size
        generateButton.setFont(new Font("Georgia", Font.BOLD, 18));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedMonth = (String) monthDropdown.getSelectedItem();
                int selectedYear = (int) yearDropdown.getSelectedItem();
                displaySavingsAndExpense(selectedMonth, selectedYear, resultTextArea);

                // Center the frame on the screen after generating report
                frame.setLocationRelativeTo(null);
            }
        });
        panel.add(generateButton);

        // Add the panel to the frame
        frame.add(panel, BorderLayout.CENTER);

        // Set the initial size of the frame
        frame.setVisible(true);
    }



    private static void displaySavingsAndExpense(String selectedMonth, int selectedYear, JTextArea resultTextArea) {
        try {
            // Establishing the connection
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            // Query to get savings for the specified month and year
            String savingsQuery = "SELECT SUM(inc_amt) AS TOTAL_INCOME FROM budget " +
                    "WHERE month = ? AND year = ?";
            PreparedStatement savingsStatement = connection.prepareStatement(savingsQuery);
            savingsStatement.setString(1, selectedMonth);
            savingsStatement.setInt(2, selectedYear);
            ResultSet savingsResult = savingsStatement.executeQuery();

            double totalSavings = 0;

            if (savingsResult.next()) {
                totalSavings = savingsResult.getDouble("TOTAL_INCOME");
            }

            // Query to get expenses for the specified month and year
            String expenseQuery = "SELECT SUM(exp_amt) AS TOTAL_EXPENSE FROM budget " +
                    "WHERE month = ? AND year = ?";
            PreparedStatement expenseStatement = connection.prepareStatement(expenseQuery);
            expenseStatement.setString(1, selectedMonth);
            expenseStatement.setInt(2, selectedYear);
            ResultSet expenseResult = expenseStatement.executeQuery();

            double totalExpense = 0;

            if (expenseResult.next()) {
                totalExpense = expenseResult.getDouble("TOTAL_EXPENSE");
            }

            // Displaying the result in JTextArea
            String result = "Income and Expense Report for " + selectedMonth + " " + selectedYear + ":\n";
            result += "+----------------------+----------------------+\n";
            result += "| Total Income        | INR " + String.format("%.2f", totalSavings) + " |\n";
            result += "| Total Expense        | INR " + String.format("%.2f", totalExpense) + " |\n";
            result += "+----------------------+----------------------+\n";

            // Displaying the result in JTextArea
            resultTextArea.setLineWrap(true); // Enable line wrapping
            resultTextArea.setWrapStyleWord(true); // Wrap at word boundaries
            resultTextArea.setText(result);

            // Adjusting JTextArea preferred size based on content
            resultTextArea.setRows(resultTextArea.getLineCount());

            // Closing resources
            savingsResult.close();
            savingsStatement.close();
            expenseResult.close();
            expenseStatement.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
