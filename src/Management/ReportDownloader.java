package Management;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReportDownloader {
    public static void downloadReports() {
        // Create a frame for selecting start and end months and year
        JFrame frame = new JFrame("Select Months and Year");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 220);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Start Month dropdown panel
        JLabel startMonthLabel = new JLabel("Start Month:");
        panel.add(startMonthLabel);
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        JComboBox<String> startMonthDropdown = new JComboBox<>(months);
        startMonthLabel.setFont(new Font("Georgia",Font.PLAIN,16));
        startMonthDropdown.setFont(new Font("Georgia",Font.PLAIN,14));
        panel.add(startMonthDropdown);

        // End Month dropdown panel
        JLabel endMonthLabel = new JLabel("End Month:");
        endMonthLabel.setFont(new Font("Georgia",Font.PLAIN,16));
        panel.add(endMonthLabel);
        JComboBox<String> endMonthDropdown = new JComboBox<>(months);
        endMonthDropdown.setFont(new Font("Georgia",Font.PLAIN,16));
        panel.add(endMonthDropdown);

        // Year dropdown panel
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Georgia",Font.PLAIN,16));
        panel.add(yearLabel);
        JComboBox<Integer> yearDropdown = new JComboBox<>();
        int currentYear = java.time.Year.now().getValue();
        for (int year = currentYear; year >= 2000; year--) {
            yearDropdown.addItem(year);
        }
        yearDropdown.setFont(new Font("Georgia",Font.PLAIN,16));
        panel.add(yearDropdown);

        // Download button
        JButton downloadButton = new JButton("Download Reports");
        downloadButton.addActionListener(e -> {
            String startMonth = (String) startMonthDropdown.getSelectedItem();
            String endMonth = (String) endMonthDropdown.getSelectedItem();
            int year = (int) yearDropdown.getSelectedItem();
            downloadReports(startMonth, endMonth, year);
            frame.dispose(); // Close the frame after downloading reports
        });
        downloadButton.setFont(new Font("Georgia",Font.BOLD,18));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(downloadButton);
        buttonPanel.add(Box.createHorizontalGlue());
        frame.add(buttonPanel, BorderLayout.SOUTH);


        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void downloadReports(String startMonth, String endMonth, int year) {
        try {
            // Establishing the database connection
            Connection connection = DBConnection.connection();

            Map<String, String> monthMap = new HashMap<>();
            monthMap.put("January", "01");
            monthMap.put("February", "02");
            monthMap.put("March", "03");
            monthMap.put("April", "04");
            monthMap.put("May", "05");
            monthMap.put("June", "06");
            monthMap.put("July", "07");
            monthMap.put("August", "08");
            monthMap.put("September", "09");
            monthMap.put("October", "10");
            monthMap.put("November", "11");
            monthMap.put("December", "12");

            String startMonthNumeric = monthMap.get(startMonth);
            String endMonthNumeric = monthMap.get(endMonth);

            // Query to retrieve savings and expense breakdown data
            String query = "SELECT INC_SRC, INC_AMT, EXP_SRC, EXP_AMT, MONTH, YEAR " +
                    "FROM BUDGET " +
                    "WHERE TO_NUMBER(TO_CHAR(TO_DATE(MONTH, 'Month'), 'MM')) BETWEEN ? AND ? " +
                    "AND YEAR = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, startMonthNumeric);
            statement.setString(2, endMonthNumeric);
            statement.setInt(3, year);
            ResultSet resultSet = statement.executeQuery();

            // File path for saving the report
            // Get the user's downloads folder
            String downloadsFolder = "C:\\Users\\MANSHAY\\Downloads\\";
            String filePath = downloadsFolder  + "Report" + ".csv";

            // Write data to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                // Write header
                writer.write("Income Source, Income Amount, Expense Source, Expense Amount, Month, Year");
                writer.newLine();

                // Write data rows
                while (resultSet.next()) {
                    writer.write(resultSet.getString("INC_SRC") + ",");
                    writer.write(resultSet.getString("INC_AMT") + ",");
                    writer.write(resultSet.getString("EXP_SRC") + ",");
                    writer.write(resultSet.getString("EXP_AMT") + ",");
                    writer.write(resultSet.getString("MONTH") + ",");
                    writer.write(resultSet.getString("YEAR"));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

            JOptionPane.showMessageDialog(null, "File downloaded successfully in:\n" + filePath,
                    "Download Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
