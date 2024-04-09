import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CreatePost extends JPanel {
    private JTextField titleField;
    private JTextField imageField;
    private JTextArea descriptionField;

    public CreatePost() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400)); // Set a preferred size for the panel
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Add some padding

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title field
        JLabel titleLabel = new JLabel("Title:");
        inputPanel.add(titleLabel, gbc);

        gbc.gridx++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        JLabel imageLabel = new JLabel("Image URL:");
        inputPanel.add(imageLabel, gbc);

        gbc.gridx++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        imageField = new JTextField(20);
        inputPanel.add(imageField, gbc);

        // Select Image button
        gbc.gridx = 2; // Position the button in the third column
        gbc.fill = GridBagConstraints.NONE;
        JButton selectImageButton = new JButton("Select Image");
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(CreatePost.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imageField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        inputPanel.add(selectImageButton, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        JLabel descriptionLabel = new JLabel("Description:");
        inputPanel.add(descriptionLabel, gbc);

        gbc.gridx++;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descriptionField = new JTextArea(5, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionField);
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        inputPanel.add(descriptionScrollPane, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createPostButton = new JButton("Create Post");
        createPostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement create post functionality here
                // You can access the title, image URL, and description using getTitle(), getImageUrl(), and getDescription() methods
                System.out.println("Title: " + getTitle());
                System.out.println("Image URL: " + getImageUrl());
                System.out.println("Description: " + getDescription());
            }
        });
        buttonPanel.add(createPostButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public String getTitle() {
        return titleField.getText();
    }

    public String getImageUrl() {
        return imageField.getText();
    }

    public String getDescription() {
        return descriptionField.getText();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Create Post");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new CreatePost());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
