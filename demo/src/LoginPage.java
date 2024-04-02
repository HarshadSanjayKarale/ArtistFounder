import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login Page");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel loginLabel = new JLabel("Login Page");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loginLabel.setForeground(Color.WHITE); // Set text color to white

        // Add padding and spacing to components
        int padding = 20;
        loginLabel.setBorder(new EmptyBorder(padding, padding, padding, padding));
        loginLabel.setOpaque(true); // Make the label opaque to set background color
        loginLabel.setBackground(Color.RED); // Set background color for the label

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setMargin(new Insets(15, 10, 5, 10));
        emailField.setToolTipText("Enter your email");

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMargin(new Insets(15, 10, 5, 10));
        passwordField.setToolTipText("Enter your password");

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                // Verify user credentials
                try {
                    if (verifyCredentials(email, password)) {
                        JOptionPane.showMessageDialog(LoginPage.this, "Login successful!");
                        dispose(); // Close the LoginPage
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                new HomePageLoggedIn(email);
                            }
                        });
                    } else {
                        JOptionPane.showMessageDialog(LoginPage.this, "Invalid email or password");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginPage.this, "Database error: " + ex.getMessage());
                }
            }
        });

        JLabel signUpLabel = new JLabel("Don't have an account? Sign Up");
        signUpLabel.setForeground(Color.BLUE);
        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open SignUpPage when clicked
                dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SignUpPage();
                    }
                });
            }
        });

        setLayout(new BorderLayout());
        add(loginLabel, BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpLabel);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
    
        // Create a panel for the image
        JPanel imagePanel = new JPanel(new BorderLayout());
        JLabel imageLabel = new JLabel(new ImageIcon("C://Users//sachi//OneDrive//Desktop//PBL Project//Test//ArtistFounder//demo//src//images//test.jpg"));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
    
        // Add the image panel to the left
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        inputPanel.add(imagePanel, gbc);
    
        // Create a panel for the input fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcFields = new GridBagConstraints();
        gbcFields.insets = new Insets(5, 5, 5, 5);
        gbcFields.anchor = GridBagConstraints.WEST;
    
        gbcFields.gridx = 0;
        gbcFields.gridy = 0;
        fieldsPanel.add(new JLabel("Email:"), gbcFields);
    
        gbcFields.gridx = 1;
        gbcFields.gridy = 0;
        gbcFields.weightx = 1;
        gbcFields.fill = GridBagConstraints.HORIZONTAL;
        emailField.setPreferredSize(new Dimension(200, 40));
        fieldsPanel.add(emailField, gbcFields);
    
        gbcFields.gridx = 0;
        gbcFields.gridy = 1;
        fieldsPanel.add(new JLabel("Password:"), gbcFields);
    
        gbcFields.gridx = 1;
        gbcFields.gridy = 1;
        gbcFields.weightx = 1;
        gbcFields.fill = GridBagConstraints.HORIZONTAL;
        passwordField.setPreferredSize(new Dimension(200, 40));
        fieldsPanel.add(passwordField, gbcFields);
    
        // Add the fields panel to the right
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(fieldsPanel, gbc);
    
        return inputPanel;
    }
    
    
    

    private boolean verifyCredentials(String email, String password) throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
        String dbUser = "root";
        String dbPassword = "SanjayKarale@123";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                return resultSet.next(); // If a row is found, credentials are valid
            }
        } catch (SQLException e) {
            throw e; // Propagate the exception for higher-level handling
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginPage();
            }
        });
    }
}
