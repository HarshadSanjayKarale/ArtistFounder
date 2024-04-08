
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
        JPanel navbar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                int width = getWidth();
                int height = getHeight();

                Color startColor = new Color(255, 0, 0); // Pink
                Color endColor = new Color(255, 255, 0); // Yellow

                GradientPaint gradientPaint = new GradientPaint(0, 0, startColor, width, height, endColor);
                g2d.setPaint(gradientPaint);
                g2d.fillRect(0, 0, width, height);

                g2d.dispose();
            }
        };
        navbar.setLayout(new BorderLayout()); // Use BorderLayout for left and right alignment

        JLabel title = new JLabel("Artist Founder");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.LEFT); // Align the title to the left
        navbar.add(title, BorderLayout.WEST); // Add title to the left side

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel for buttons on the right
        buttonsPanel.setOpaque(false); // Make the panel transparent

        JButton profileButton = new JButton("Profile");
        profileButton.addActionListener(e -> {
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

                    JOptionPane.showMessageDialog(navbar, userDetails, "User Profile",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(navbar, "User not found", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(navbar, "Database error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonsPanel.add(profileButton);

        JButton notificationButton = new JButton("Notification");
        notificationButton.addActionListener(e -> displayNotifications());
        buttonsPanel.add(notificationButton);

        JButton addPostButton = new JButton("CreatePost");
        addPostButton.addActionListener(e -> addPost());
        buttonsPanel.add(addPostButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            // Close the current HomePageLoggedIn
            dispose();

            // Open the HomePage
            SwingUtilities.invokeLater(() -> new HomePage());
        });
        buttonsPanel.add(logoutButton);

        navbar.add(buttonsPanel, BorderLayout.EAST); // Add buttonsPanel to the right side

        return navbar;
    }

    private void displayNotifications() {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
            String dbUser = "root";
            String dbPassword = "SanjayKarale@123";

            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            String query = "SELECT messages.*, users.first_name FROM messages " +
                    "INNER JOIN users ON messages.post_user_id = users.id " +
                    "WHERE messages.post_user_id = ? ORDER BY messages.timestamp DESC"; // Order by timestamp in
                                                                                        // descending order
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, getUserIdByEmail(loggedInUserEmail));
            ResultSet resultSet = preparedStatement.executeQuery();

            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            tableModel.addColumn("Timestamp");
            tableModel.addColumn("Message");

            while (resultSet.next()) {
                String timestamp = resultSet.getString("timestamp");
                String message = resultSet.getString("msg");
                tableModel.addRow(new Object[] { timestamp, message });
            }

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            scrollPane.getViewport().setBackground(new Color(220, 255, 220)); // Faint green background

            if (table.getRowCount() > 0) {
                JOptionPane.showMessageDialog(HomePageLoggedIn.this, scrollPane, "Notifications",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(HomePageLoggedIn.this, "No notifications", "Notifications",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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
                int postId = resultSet.getInt("post_id");
                int postUserId = resultSet.getInt("user_id");
                final int[] likesCount = { resultSet.getInt("no_of_likes") }; // Store likes count in an array

                // Create a panel for each post
                JPanel cardPanel = new JPanel();
                cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
                cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Add a border to the card
                cardPanel.setBackground(new Color(200, 255, 200)); // Faint green background

                JLabel titleLabel = new JLabel(title + " by " + firstName);
                titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardPanel.add(titleLabel);

                JLabel imageLabel = new JLabel(new ImageIcon(imageUrl));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardPanel.add(imageLabel);

                JLabel descriptionLabel = new JLabel(
                        "<html><body style='width: 200px; text-align: center'>" + description + "</body></html>");
                descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardPanel.add(descriptionLabel);

                // Create a panel for the buttons
                JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
                buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton likeButton = new JButton("Like (" + likesCount[0] + ")");
                likeButton.setForeground(Color.RED);
                likeButton.setBackground(new Color(255, 200, 200)); // Faint red background
                likeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            int userId = getUserIdByEmail(loggedInUserEmail);
                            String updateLikesQuery = "UPDATE posts SET no_of_likes = no_of_likes + 1 WHERE post_id = ?";
                            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                                    PreparedStatement updateLikesStatement = connection
                                            .prepareStatement(updateLikesQuery)) {
                                updateLikesStatement.setInt(1, postId);
                                int rowsUpdated = updateLikesStatement.executeUpdate();

                                if (rowsUpdated > 0) {
                                    // Update the displayed likes count
                                    likesCount[0]++;
                                    likeButton.setText("Like (" + likesCount[0] + ")");
                                    JOptionPane.showMessageDialog(HomePageLoggedIn.this,
                                            "Post liked successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Failed to like post", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(HomePageLoggedIn.this,
                                    "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                buttonPanel.add(likeButton);

                JButton viewButton = new JButton("View Profile");
                viewButton.setBackground(new Color(200, 200, 255)); // Faint blue background
                viewButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Profile profile = new Profile(postUserId);
                        profile.setVisible(true);
                    }
                });
                buttonPanel.add(viewButton);

                cardPanel.add(buttonPanel);

                // Create a panel for the recruit button
                JPanel recruitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JButton recruitButton = new JButton("Recruit");
                recruitButton.setBackground(new Color(255, 255, 200)); // Faint yellow background
                recruitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Create a panel for entering the recruitment message
                        JPanel messagePanel = new JPanel();
                        messagePanel.setLayout(new GridLayout(2, 1));

                        JLabel messageLabel = new JLabel("Enter your recruitment message:");
                        JTextArea messageTextArea = new JTextArea(4, 20);
                        messageTextArea.setLineWrap(true);
                        messageTextArea.setWrapStyleWord(true);

                        messagePanel.add(messageLabel);
                        messagePanel.add(new JScrollPane(messageTextArea));

                        int result = JOptionPane.showConfirmDialog(HomePageLoggedIn.this, messagePanel,
                                "Recruitment Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                        if (result == JOptionPane.OK_OPTION) {
                            String message = messageTextArea.getText();

                            try {
                                int userId = getUserIdByEmail(loggedInUserEmail);

                                String insertMessageQuery = "INSERT INTO messages (post_id, post_user_id, user_id, msg) VALUES (?, ?, ?, ?)";
                                try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                                        PreparedStatement insertMessageStatement = connection
                                                .prepareStatement(insertMessageQuery)) {
                                    insertMessageStatement.setInt(1, postId);
                                    insertMessageStatement.setInt(2, postUserId);
                                    insertMessageStatement.setInt(3, userId);
                                    insertMessageStatement.setString(4, message);
                                    int rowsInserted = insertMessageStatement.executeUpdate();

                                    if (rowsInserted > 0) {
                                        JOptionPane.showMessageDialog(HomePageLoggedIn.this,
                                                "Recruitment message sent successfully", "Success",
                                                JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(HomePageLoggedIn.this,
                                                "Failed to send recruitment message",
                                                "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(HomePageLoggedIn.this,
                                        "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
                recruitPanel.add(recruitButton);

                cardPanel.add(recruitPanel);

                postsPanel.add(cardPanel);
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(HomePageLoggedIn.this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(postsPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        revalidate(); // Refresh the layout to show the new components
    }

    private int getUserIdByEmail(String email) throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
        String dbUser = "root";
        String dbPassword = "SanjayKarale@123";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            String query = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        }
        return -1;
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
