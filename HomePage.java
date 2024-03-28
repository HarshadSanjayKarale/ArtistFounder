package Projects;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame {
    private JButton loginButton;
    private JButton logoutButton;
    private JButton profileButton;

    public HomePage() {
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

        // Make the login button visible
        loginButton.setVisible(true);

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

        loginButton = new JButton("Login");
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

        logoutButton = new JButton("Logout");
        logoutButton.setVisible(false); // Initially invisible
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform logout actions
                // For now, just show the login button and hide the logout and profile buttons
                loginButton.setVisible(true);
                logoutButton.setVisible(false);
                profileButton.setVisible(false);
            }
        });

        navbar.add(logoutButton);

        profileButton = new JButton("Profile");
        profileButton.setVisible(false); // Initially invisible
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the profile information
                // For now, just show a message dialog
                JOptionPane.showMessageDialog(HomePage.this, "User Profile");
            }
        });

        navbar.add(profileButton);

        return navbar;
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
