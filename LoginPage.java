package Projects;

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

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
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

        // Add padding and spacing to components
        int padding = 20;
        loginLabel.setBorder(new EmptyBorder(padding, padding, padding, padding));
        loginButton.setBorder(new EmptyBorder(padding, padding, padding, padding));
        signUpLabel.setBorder(new EmptyBorder(padding, padding, padding, padding));

        int spacing = 10;
        setLayout(new BorderLayout(spacing, spacing));
        add(loginLabel, BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpLabel);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set background color
        getContentPane().setBackground(Color.CYAN);

        setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
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
