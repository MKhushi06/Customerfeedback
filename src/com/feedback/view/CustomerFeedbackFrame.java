package com.feedback.view;

import com.feedback.controller.FeedbackController;
import com.feedback.view.components.CardPanel;
import com.feedback.view.components.ModernButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CustomerFeedbackFrame extends JFrame {
    private final FeedbackController controller;
    private final JFrame parentLauncher;

    private JTextField txtName;
    private JTextField txtContact;
    private JComboBox<String> cbType;
    private StarRatingPanel starPanel;
    private JTextArea taComments;
    private JLabel lblDateValue;

    public CustomerFeedbackFrame(FeedbackController controller, JFrame parentLauncher) {
        this.controller = controller;
        this.parentLauncher = parentLauncher;

        setTitle("Customer Feedback Form");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // Window listener to return back to launcher when closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack();
            }
        });

        initUI();
    }

    private void goBack() {
        dispose();
        if (parentLauncher != null) {
            parentLauncher.setVisible(true);
        }
    }

    private void initUI() {
        Color bgColor = new Color(241, 245, 249); // slate-100
        Color cardColor = Color.WHITE;
        Color labelColor = new Color(71, 85, 105);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        CardPanel card = new CardPanel(cardColor);
        card.setBorderColor(new Color(226, 232, 240));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Header Title
        JLabel lblTitle = new JLabel("Share Your Feedback", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 6, 5, 6);
        card.add(lblTitle, gbc);

        // Header Subtitle
        JLabel lblSub = new JLabel("Help us improve our service by sharing your experience", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 6, 20, 6);
        card.add(lblSub, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Customer Name
        JLabel lblName = new JLabel("Customer Name *");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(labelColor);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        card.add(lblName, gbc);

        txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setPreferredSize(new Dimension(200, 32));
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridx = 1; gbc.weightx = 0.7;
        card.add(txtName, gbc);

        // Contact Number
        JLabel lblContact = new JLabel("Contact Number *");
        lblContact.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContact.setForeground(labelColor);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        card.add(lblContact, gbc);

        txtContact = new JTextField();
        txtContact.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContact.setPreferredSize(new Dimension(200, 32));
        txtContact.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridx = 1; gbc.weightx = 0.7;
        card.add(txtContact, gbc);

        // Feedback Type
        JLabel lblType = new JLabel("Feedback Category");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblType.setForeground(labelColor);
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        card.add(lblType, gbc);

        cbType = new JComboBox<>(new String[] {"Review", "Complaint", "Suggestion"});
        cbType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbType.setPreferredSize(new Dimension(200, 32));
        gbc.gridx = 1; gbc.weightx = 0.7;
        card.add(cbType, gbc);

        // Rating
        JLabel lblRating = new JLabel("Overall Rating *");
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRating.setForeground(labelColor);
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        card.add(lblRating, gbc);

        starPanel = new StarRatingPanel();
        starPanel.setRating(5); // Default rating
        gbc.gridx = 1; gbc.weightx = 0.7;
        card.add(starPanel, gbc);

        // Comments
        JLabel lblComments = new JLabel("Your Comments *");
        lblComments.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblComments.setForeground(labelColor);
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        card.add(lblComments, gbc);

        taComments = new JTextArea(4, 20);
        taComments.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taComments.setLineWrap(true);
        taComments.setWrapStyleWord(true);
        JScrollPane scrollComments = new JScrollPane(taComments);
        scrollComments.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        gbc.gridx = 1; gbc.weightx = 0.7;
        card.add(scrollComments, gbc);

        // Feedback Date
        JLabel lblDate = new JLabel("Feedback Date");
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDate.setForeground(labelColor);
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        card.add(lblDate, gbc);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        lblDateValue = new JLabel(df.format(new java.util.Date()));
        lblDateValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDateValue.setForeground(new Color(15, 23, 42));
        gbc.gridx = 1; gbc.weightx = 0.7;
        card.add(lblDateValue, gbc);

        // Submit Button
        ModernButton btnSubmit = new ModernButton("Submit Feedback", new Color(16, 185, 129)); // Green
        btnSubmit.setPreferredSize(new Dimension(150, 36));
        btnSubmit.addActionListener(e -> submitFeedback());

        // Back Button
        ModernButton btnBack = new ModernButton("Back", new Color(100, 116, 139));
        btnBack.setPreferredSize(new Dimension(90, 36));
        btnBack.addActionListener(e -> goBack());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        card.add(buttonPanel, gbc);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void submitFeedback() {
        String name = txtName.getText();
        String contact = txtContact.getText();
        String type = (String) cbType.getSelectedItem();
        int rating = starPanel.getRating();
        String comments = taComments.getText();
        Date dateVal = new Date(System.currentTimeMillis());

        try {
            boolean success = controller.addFeedback(name, contact, type, rating, comments, dateVal);
            if (success) {
                // Success pop-up
                JOptionPane.showMessageDialog(this,
                    "Thank you! Your feedback has been submitted successfully.",
                    "Feedback Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

                // Simulate acknowledgment email (Optional enhancement)
                JOptionPane.showMessageDialog(this,
                    "📬 Mock Email Notification Sent:\n" +
                    "To: " + name + "\n" +
                    "Subject: Acknowledgment - Feedback Received\n" +
                    "Body: Dear " + name + ", thank you for your feedback. We have registered your " + 
                    type.toLowerCase() + " (Rating: " + rating + "★) in our system.",
                    "Acknowledgment Notification",
                    JOptionPane.INFORMATION_MESSAGE);

                // Reset form fields
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to register feedback in database. Please check connection.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetForm() {
        txtName.setText("");
        txtContact.setText("");
        cbType.setSelectedIndex(0);
        starPanel.setRating(5);
        taComments.setText("");
    }
}
