import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditPostDialog extends JDialog {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton selectImageButton;
    private JLabel imageLabel;
    private int postId;
    private Connection connection; // Assume this is passed in or accessed statically

    public EditPostDialog(Frame owner, String title, boolean modal, int postId, String currentTitle, String currentDescription, Connection connection) {
        super(owner, title, modal);
        this.postId = postId;
        this.connection = connection;

        initializeUI(currentTitle, currentDescription);
        setupListeners();
    }

    private void initializeUI(String currentTitle, String currentDescription) {
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Title field
        titleField = new JTextField(currentTitle, 20);

        // Description area
        descriptionArea = new JTextArea(currentDescription, 5, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);

        // Save button
        saveButton = new JButton("Save");

        // Image components
        selectImageButton = new JButton("Select Image");
        imageLabel = new JLabel();

        // Adding components to the dialog
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(3, 1));
        fieldsPanel.add(titleField);
        fieldsPanel.add(scrollPane);
        fieldsPanel.add(selectImageButton);

        JPanel imagePanel = new JPanel();
        imagePanel.add(imageLabel);

        add(fieldsPanel, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.SOUTH);
        add(saveButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());

        // Set up image selection button
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(EditPostDialog.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(selectedFile);
                        ImageIcon icon = new ImageIcon(img);
                        imageLabel.setIcon(icon);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePostChanges();
            }
        });
    }

    private void savePostChanges() {
        try {
            String updateQuery = "UPDATE posts SET title = ?, description = ? WHERE post_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, titleField.getText());
            preparedStatement.setString(2, descriptionArea.getText());
            preparedStatement.setInt(3, postId);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(EditPostDialog.this, "Post updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(EditPostDialog.this, "Failed to update the post.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(EditPostDialog.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
