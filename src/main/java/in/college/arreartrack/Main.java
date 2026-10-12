package in.college.arreartrack;

import in.college.arreartrack.db.Database;
import in.college.arreartrack.ui.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

public final class Main {
    private Main() { }
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
            }
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> {
            try (var connection = Database.getConnection()) {
                if (!connection.isValid(3)) throw new SQLException("MySQL connection is not valid.");
                new MainFrame().setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to MySQL. Start MySQL, load database/schema.sql, and check DB_URL, DB_USER, and DB_PASSWORD.\n\nDetails: " + ex.getMessage(),
                        "ArrearTrack — database connection", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
