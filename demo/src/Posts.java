import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Posts extends JFrame {
    private int userId;

    public Posts(int userId) {
        this.userId = userId;
        setTitle("Posts");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 1));

        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
            String dbUser = "root";
            String dbPassword = "SanjayKarale@123";

            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            String query = "SELECT title, description FROM posts WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                JLabel titleLabel = new JLabel("Title: " + title);
                JLabel descriptionLabel = new JLabel("Description: " + description);
                panel.add(titleLabel);
                panel.add(descriptionLabel);
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        // Example usage
        SwingUtilities.invokeLater(() -> new Posts(1));
    }
}
