
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpPage extends JFrame {
    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JPasswordField passwordField;

    public SignUpPage() {
        setTitle("Sign Up Page");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel signUpLabel = new JLabel("Sign Up Page");

        emailField = new JTextField(20);
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String password = new String(passwordField.getPassword());

                // Register new user in the database
                try {
                    if (registerUser(email, firstName, lastName, password)) {
                        JOptionPane.showMessageDialog(SignUpPage.this, "Sign Up successful!");
                        dispose(); // Close the SignUpPage
                    } else {
                        JOptionPane.showMessageDialog(SignUpPage.this, "Error in sign up");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(SignUpPage.this, "Database error: " + ex.getMessage());
                }
            }
        });

        // Add padding and spacing to components
        int padding = 20;
        signUpLabel.setBorder(new EmptyBorder(padding, padding, padding, padding));
        signUpButton.setBorder(new EmptyBorder(padding, padding, padding, padding));

        int spacing = 10;
        setLayout(new BorderLayout(spacing, spacing));
        add(signUpLabel, BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.CENTER);
        add(signUpButton, BorderLayout.SOUTH);

        // Set background color
        getContentPane().setBackground(Color.CYAN);

        setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
        return inputPanel;
    }

    private boolean registerUser(String email, String firstName, String lastName, String password) throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/pblproject";
        String dbUser = "root";
        String dbPassword = "SanjayKarale@123";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            String query = "INSERT INTO users (email, first_name, last_name, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, firstName);
                preparedStatement.setString(3, lastName);
                preparedStatement.setString(4, password);

                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0; // If rows are affected, registration is successful
            }
        } catch (SQLException e) {
            throw e; // Propagate the exception for higher-level handling
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SignUpPage();
            }
        });
    }
}
