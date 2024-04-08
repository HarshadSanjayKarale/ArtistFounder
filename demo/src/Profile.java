import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.sql.*;

public class Profile extends JFrame {
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JPanel postsPanel;
    

    public Profile(int userId) {
        setTitle("Profile");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        JPanel profilePanel = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, Color.RED, w, 0, Color.YELLOW);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameLabel = new JLabel();
        emailLabel = new JLabel();
        nameLabel.setForeground(Color.WHITE);
        emailLabel.setForeground(Color.WHITE);


        profilePanel.add(nameLabel);
        profilePanel.add(emailLabel);

        mainPanel.add(profilePanel, BorderLayout.NORTH);

        postsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        postsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(postsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        fetchUserProfile(userId);
        fetchUserPosts(userId);
    }

    private void fetchUserProfile(int userId) {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
            String dbUser = "root";
            String dbPassword = "SanjayKarale@123";

            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            String query = "SELECT first_name, last_name, email FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");

                nameLabel.setText("Name: " + firstName + " " + lastName);
                emailLabel.setText("Email: " + email);
            } else {
                nameLabel.setText("User not found");
                emailLabel.setText("");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            nameLabel.setText("Database error");
            emailLabel.setText("");
        }
    }

    private void fetchUserPosts(int userId) {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
            String dbUser = "root";
            String dbPassword = "SanjayKarale@123";
    
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    
            String query = "SELECT post_id, title, image, description, no_of_likes FROM posts WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                String title = resultSet.getString("title");
                String imageUrl = resultSet.getString("image");
                String description = resultSet.getString("description");
                int likesCount = resultSet.getInt("no_of_likes");
    
                JPanel cardPanel = new JPanel();
                cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
                cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                cardPanel.setBackground(new Color(200, 255, 200));
    
                JLabel titleLabel = new JLabel(title);
                titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardPanel.add(titleLabel);
    
                JLabel imageLabel = new JLabel(new ImageIcon(imageUrl));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardPanel.add(imageLabel);
    
                JLabel descriptionLabel = new JLabel(
                        "<html><body style='width: 200px; text-align: center'>" + description + "</body></html>");
                descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardPanel.add(descriptionLabel);
    
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    
                JButton likeButton = new JButton("Like (" + likesCount + ")");
                likeButton.setForeground(Color.RED); 
                likeButton.addActionListener(e -> {
                    try {
                        String updateLikesQuery = "UPDATE posts SET no_of_likes = no_of_likes + 1 WHERE post_id = ?";
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                             PreparedStatement updateLikesStatement = conn.prepareStatement(updateLikesQuery)) {
                            updateLikesStatement.setInt(1, postId);
                            int rowsUpdated = updateLikesStatement.executeUpdate();
    
                            if (rowsUpdated > 0) {
                                likeButton.setText("Like (" + (likesCount + 1) + ")");
                                JOptionPane.showMessageDialog(this, "Post liked successfully", "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to like post", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
    
                buttonPanel.add(likeButton);
                cardPanel.add(buttonPanel);
    
                postsPanel.add(cardPanel);
            }
    
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Profile profile = new Profile(1); // Replace 1 with the actual userId
            profile.setVisible(true);
        });
    }
}
