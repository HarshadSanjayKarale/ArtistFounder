import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Posts extends JFrame {
    private JPanel postsPanel;
    private Connection connection;

    public Posts(int userId) {
        
        setTitle("User Posts");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a custom panel for the navigation bar with a gradient background
        JPanel navBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                        0, 0, Color.RED,
                        w, 0, Color.YELLOW);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        navBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel navBarLabel = new JLabel("Posts");
        navBarLabel.setForeground(Color.WHITE);
        navBarPanel.add(navBarLabel);

        // Add the custom navigation bar panel to the frame
        add(navBarPanel, BorderLayout.NORTH);

        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(postsPanel);
        add(scrollPane);

        connection = establishConnection();

        List<Post> posts = fetchUserPosts(userId);
        displayPosts(posts);

        setVisible(true);
    }

    private Connection establishConnection() {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
            String dbUser = "root";
            String dbPassword = "SanjayKarale@123";
            return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private List<Post> fetchUserPosts(int userId) {
        List<Post> posts = new ArrayList<>();
        try {
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

                posts.add(new Post(postId, title, imageUrl, description, likesCount));
            }

            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return posts;
    }

    private void displayPosts(List<Post> posts) {
        for (Post post : posts) {
            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            cardPanel.setBackground(new Color(200, 255, 200));

            JLabel titleLabel = new JLabel(post.getTitle());
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(titleLabel);

            JLabel imageLabel = new JLabel(new ImageIcon(post.getImageUrl()));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(imageLabel);

            JLabel descriptionLabel = new JLabel(
                    "<html><body style='width: 200px; text-align: center'>" + post.getDescription() + "</body></html>");
            descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(descriptionLabel);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton editButton = new JButton("Edit");
            editButton.addActionListener(e -> {
                // Assuming `post` is an accessible object in this context that contains the current post's information
                // And assuming `connection` is your database connection object available in this scope
                EditPostDialog editDialog = new EditPostDialog(
                        null, // or pass a parent frame if available
                        "Edit Post", // Dialog title
                        true, // modal
                        post.getPostId(), // Assuming `getPostId()` gets the id of the post to edit
                        post.getTitle(), // Assuming you have a method to get the current title
                        post.getDescription(), // Assuming you have a method to get the current description
                        connection // The database connection
                );
                editDialog.setVisible(true); // Show the dialog
            });
            
            buttonPanel.add(editButton);

            // Delete button
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> {
                // Implement the delete functionality here
                // You can show a confirmation dialog before deleting the post
                int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this post?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    // Delete the post from the database and remove the cardPanel from postsPanel
                    try {
                        String deleteQuery = "DELETE FROM posts WHERE post_id = ?";
                        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                        deleteStatement.setInt(1, post.getPostId());
                        int deletedRows = deleteStatement.executeUpdate();
                        if (deletedRows > 0) {
                            postsPanel.remove(cardPanel);
                            revalidate();
                            repaint();
                            JOptionPane.showMessageDialog(this, "Post deleted successfully");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete post");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonPanel.add(deleteButton);
            cardPanel.add(buttonPanel);

            postsPanel.add(cardPanel);
        }

        revalidate();
        repaint();
    }

    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error closing database connection: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Posts(1)); // Example usage
    }
}

class Post {
    private int postId;
    private String title;
    private String imageUrl;
    private String description;
    private int likesCount;

    public Post(int postId, String title, String imageUrl, String description, int likesCount) {
        this.postId = postId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.likesCount = likesCount;
    }

    public int getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getLikesCount() {
        return likesCount;
    }
}
