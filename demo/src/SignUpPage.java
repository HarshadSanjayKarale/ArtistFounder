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
        signUpLabel.setFont(new Font("Arial", Font.BOLD, 24));
        signUpLabel.setForeground(Color.WHITE); // Set text color to white

        // Add padding and spacing to components
        int padding = 20;
        signUpLabel.setBorder(new EmptyBorder(padding, padding, padding, padding));
        signUpLabel.setOpaque(true); // Make the label opaque to set background color
        signUpLabel.setBackground(Color.BLUE); // Set background color for the label

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setMargin(new Insets(15, 10, 5, 10));
        emailField.setToolTipText("Enter your email");

        firstNameField = new JTextField(20);
        firstNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        firstNameField.setMargin(new Insets(15, 10, 5, 10));
        firstNameField.setToolTipText("Enter your first name");

        lastNameField = new JTextField(20);
        lastNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        lastNameField.setMargin(new Insets(15, 10, 5, 10));
        lastNameField.setToolTipText("Enter your last name");

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMargin(new Insets(15, 10, 5, 10));
        passwordField.setToolTipText("Enter your password");

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 16));
        signUpButton.setBackground(Color.BLUE);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setOpaque(true);
        signUpButton.setBorderPainted(false);
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

        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(30, 144, 255);
                Color color2 = Color.WHITE;
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        contentPane.setLayout(new BorderLayout());

        contentPane.add(signUpLabel, BorderLayout.NORTH);
        contentPane.add(createInputPanel(), BorderLayout.CENTER);
        contentPane.add(signUpButton, BorderLayout.SOUTH);

        setContentPane(contentPane);

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
