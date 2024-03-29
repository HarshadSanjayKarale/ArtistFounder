
import javax.swing.*;
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
        setPreferredSize(new Dimension(900, 500)); // Set the size to match the home page

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        titleField = new JTextField();
        imageField = new JTextField();
        descriptionField = new JTextArea();
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionField);
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Image URL:"));
        inputPanel.add(imageField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionScrollPane);

        add(inputPanel, BorderLayout.CENTER);

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

        add(selectImageButton, BorderLayout.SOUTH);
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
}
