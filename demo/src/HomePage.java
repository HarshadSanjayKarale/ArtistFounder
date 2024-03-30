import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HomePage extends JFrame {
    private JPanel contentArea;

    public HomePage() {
        setTitle("Artist Founder");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel navbar = createNavbar();
        contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));

        // Add navbar and content area to the content pane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navbar, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(contentArea), BorderLayout.CENTER);

        // Make the login button visible
        setVisible(true);

        // Fetch and display posts
        fetchAndDisplayPosts();
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel();
        navbar.setBackground(Color.BLUE);
        navbar.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JLabel title = new JLabel("Artist Founder");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        navbar.add(title);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current HomePage
                dispose();

                // Open the LoginPage
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new LoginPage();
                    }
                });
            }
        });

        navbar.add(loginButton);

        return navbar;
    }

    private void fetchAndDisplayPosts() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
        String dbUser = "root";
        String dbPassword = "SanjayKarale@123";
    
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            String query = "SELECT posts.*, CONCAT(users.first_name, ' ', users.last_name) AS full_name FROM posts INNER JOIN users ON posts.user_id = users.id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            // Create a panel to hold the cards
            JPanel cardsPanel = new JPanel(new GridLayout(0, 2));
    
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String imageUrl = resultSet.getString("image");
                String description = resultSet.getString("description");
                int likes = resultSet.getInt("no_of_likes");
                String userName = resultSet.getString("full_name"); // Fetching full name
    
                JPanel cardPanel = createCard(title, imageUrl, description, likes, userName);
                cardsPanel.add(cardPanel);
            }
    
            // Add the cards panel to the content area
            contentArea.add(cardsPanel);
            contentArea.revalidate();
            contentArea.repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private JPanel createCard(String title, String imageUrl, String description, int likesCount, String userName) {
        int[] likes = {likesCount}; // Array to hold the like count
    
        JPanel cardPanel = new JPanel();
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(new Color(204, 255, 204)); // Faint green background color
    
        JLabel titleLabel = new JLabel(title + " by " + userName); // Include user's name in the title
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the title
        titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f)); // Set bigger font size for the title
        cardPanel.add(titleLabel, BorderLayout.NORTH);
    
        JLabel imageLabel = new JLabel(new ImageIcon(imageUrl));
        cardPanel.add(imageLabel, BorderLayout.CENTER);
    
        JLabel descriptionLabel = new JLabel(description);
        cardPanel.add(descriptionLabel, BorderLayout.SOUTH);
    
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2)); // Use a GridLayout for the buttons
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        JButton likeButton = new JButton("Likes: " + likes[0]);
        likeButton.setForeground(Color.WHITE); // White text color
        likeButton.setBackground(new Color(255, 50, 20)); // Faint red background color
        likeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the database
                updateLikesCount(title);
    
                // Update the likes count displayed on the card
                likes[0]++;
                likeButton.setText("Likes: " + likes[0]);
            }
        });
        buttonPanel.add(likeButton);
    

        JButton viewButton = new JButton("View Details");
        viewButton.setBackground(new Color(204, 153, 255)); // Faint violet background color
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a panel to show post details
                JPanel postDetailPanel = new JPanel(new GridLayout(3, 1));
                postDetailPanel.setPreferredSize(new Dimension(400, 150)); // Set panel size
                postDetailPanel.setBackground(new Color(255, 255, 204)); // Yellow background color
    
                JLabel titleLabel = new JLabel("Title: " + title);
                titleLabel.setForeground(Color.GREEN); // Green color for title
                postDetailPanel.add(titleLabel);
    
                JLabel userLabel = new JLabel("Username: " + userName);
                userLabel.setForeground(Color.BLUE); // Blue color for username
                postDetailPanel.add(userLabel);
    
                JLabel descriptionLabel = new JLabel("Description: " + description);
                descriptionLabel.setForeground(Color.RED); // Red color for description
                postDetailPanel.add(descriptionLabel);
    
                JOptionPane.showMessageDialog(null, postDetailPanel, "Post Details", JOptionPane.PLAIN_MESSAGE);
            }
        });
        buttonPanel.add(viewButton);
    
        return cardPanel;
    }
    
    
    
    
    
    
    

    private void updateLikesCount(String title) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
        String dbUser = "root";
        String dbPassword = "SanjayKarale@123";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            String query = "UPDATE posts SET no_of_likes = no_of_likes + 1 WHERE title = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HomePage();
            }
        });
    }
}
