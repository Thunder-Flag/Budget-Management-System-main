package Management;
import javax.swing.*;

public class ManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame.mainframe();
            }
        });
    }
}
