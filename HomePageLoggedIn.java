package Projects;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomePageLoggedIn extends JFrame {
    private String loggedInUserEmail;
    private JButton logoutButton;
    private JButton profileButton;

    public HomePageLoggedIn(String email) {
        this.loggedInUserEmail = email;
        setTitle("Artist Founder");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel navbar = createNavbar();

        // Set the background color of the content pane to green
        getContentPane().setBackground(Color.GREEN);

        // Add navbar to the content pane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navbar, BorderLayout.NORTH);

        displayPosts();
        setVisible(true);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel();
        navbar.setBackground(Color.BLUE);
        navbar.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JLabel title = new JLabel("Artist Founder");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        navbar.add(title);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current HomePageLoggedIn
                dispose();

                // Open the HomePage
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new HomePage();
                    }
                });
            }
        });

        navbar.add(logoutButton);

        profileButton = new JButton("Profile");
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fetch and display user profile details
                try {
                    String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
                    String dbUser = "root";
                    String dbPassword = "SanjayKarale@123";

                    Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

                    String query = "SELECT * FROM users WHERE email = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, loggedInUserEmail); // Use the email of the logged-in user
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        String email = resultSet.getString("email");
                        String userDetails = "Name: " + firstName + " " + lastName + "\nEmail: " + email;

                        JOptionPane.showMessageDialog(HomePageLoggedIn.this, userDetails, "User Profile", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(HomePageLoggedIn.this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    preparedStatement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        navbar.add(profileButton);

        JButton addPostButton = new JButton("Add Post");
        addPostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPost();
            }
        });
        navbar.add(addPostButton);

        return navbar;
    }

    private void addPost() {
        CreatePost createPostPanel = new CreatePost();
    
        int result = JOptionPane.showConfirmDialog(HomePageLoggedIn.this, createPostPanel, "Add Post",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        if (result == JOptionPane.OK_OPTION) {
            String title = createPostPanel.getTitle();
            String imageUrl = createPostPanel.getImageUrl();
            String description = createPostPanel.getDescription();
    
            // Get the user ID of the logged-in user
            int userId = -1; // Default value if user ID is not found
            try {
                String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
                String dbUser = "root";
                String dbPassword = "SanjayKarale@123";
    
                Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    
                String query = "SELECT id FROM users WHERE email = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, loggedInUserEmail);
                ResultSet resultSet = preparedStatement.executeQuery();
    
                if (resultSet.next()) {
                    userId = resultSet.getInt("id");
                }
    
                preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Database error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return; // Exit method if there's an error
            }
    
            // Add the post to the database with the retrieved user ID
            try {
                String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
                String dbUser = "root";
                String dbPassword = "SanjayKarale@123";
    
                Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    
                String query = "INSERT INTO posts (user_id, title, image, description, no_of_likes) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userId); // Use the retrieved user ID
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, imageUrl);
                preparedStatement.setString(4, description);
                preparedStatement.setInt(5, 0); // Initial number of likes set to 0
    
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Post added successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Failed to add post", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
    
                preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Database error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void displayPosts() {
        JPanel postsPanel = new JPanel();
        postsPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 cards per row, with 10px horizontal and vertical gap
    
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
            String dbUser = "root";
            String dbPassword = "SanjayKarale@123";
    
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    
            String query = "SELECT posts.*, users.first_name FROM posts " +
                           "INNER JOIN users ON posts.user_id = users.id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String imageUrl = resultSet.getString("image");
                String description = resultSet.getString("description");
                String firstName = resultSet.getString("first_name");
    
                // Create a panel for each post
                JPanel cardPanel = new JPanel(new BorderLayout());
                cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Add a border to the card
    
                JLabel titleLabel = new JLabel(title + " by " + firstName);
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cardPanel.add(titleLabel, BorderLayout.NORTH);
    
                JLabel imageLabel = new JLabel(new ImageIcon(imageUrl));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cardPanel.add(imageLabel, BorderLayout.CENTER);
    
                JLabel descriptionLabel = new JLabel("<html><body style='width: 100px; text-align: center'>" + description + "</body></html>");
                descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cardPanel.add(descriptionLabel, BorderLayout.SOUTH);
    
                postsPanel.add(cardPanel);
            }
    
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
        JScrollPane scrollPane = new JScrollPane(postsPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        revalidate(); // Refresh the layout to show the new components
    }
    
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HomePageLoggedIn("user@example.com"); // Replace with the actual email of the logged-in user
            }
        });
    }
}
