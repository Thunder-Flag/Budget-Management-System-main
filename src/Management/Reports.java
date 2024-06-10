package Management;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Reports {
    public static void openGenerateReportsSection() {
        JFrame reportsFrame = new JFrame("Generate Reports");
        reportsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose frame on close
        reportsFrame.setSize(450, 250); // Adjust size as needed
        reportsFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20)); // Add padding

        JLabel titleLabel = new JLabel("Generate Reports");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 20)); // Set font size and style
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel,BoxLayout.Y_AXIS));

        JButton incomeExpenseButton = new JButton("Calculate Savings");
        JButton expenseBreakdownButton = new JButton("Expense Breakdown");
        JButton downloadReportsButton = new JButton("Download Reports");

        Font buttonFont = new Font("Cambria", Font.BOLD, 18); // Adjust the font size as needed
        incomeExpenseButton.setFont(buttonFont);
        expenseBreakdownButton.setFont(buttonFont);
        downloadReportsButton.setFont(buttonFont);
        incomeExpenseButton.setFocusPainted(false);

        optionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
        optionsPanel.add(incomeExpenseButton);
        optionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
        optionsPanel.add(expenseBreakdownButton);
        optionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
        optionsPanel.add(downloadReportsButton);

        incomeExpenseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        expenseBreakdownButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        downloadReportsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        incomeExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Savings.savings();
            }
        });

        expenseBreakdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Add action to generate Expense Breakdown report
                ExpenseBreakdown.expenseBreakdown();
            }
        });
        downloadReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    ReportDownloader.downloadReports();
            }
        });

        reportsFrame.add(mainPanel);
        reportsFrame.setVisible(true);
    }
}
