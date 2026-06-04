package com.feedback.view;

import com.feedback.controller.FeedbackController;
import com.feedback.model.Feedback;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FeedbackFormDialog extends JDialog {
    private final FeedbackController controller;
    private final Feedback existingFeedback; // null if Adding
    private boolean saved = false;

    private JTextField txtName;
    private JTextField txtContact;
    private JComboBox<String> cbType;
    private StarRatingPanel starPanel;
    private JTextArea taComments;
    private JTextField txtDate;

    public FeedbackFormDialog(JFrame parent, FeedbackController controller, Feedback existingFeedback) {
        super(parent, existingFeedback == null ? "Add Customer Feedback" : "Edit Customer Feedback", true);
        this.controller = controller;
        this.existingFeedback = existingFeedback;

        setSize(480, 560);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        if (existingFeedback != null) {
            loadFeedbackData();
        }
    }

    public boolean isSaved() {
        return saved;
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
        JLabel lblTitle = new JLabel(existingFeedback == null ? "New Feedback Entry" : "Modify Feedback Details");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 6, 15, 6);
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Customer Name
        JLabel lblName = new JLabel("Customer Name *");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        card.add(lblName, gbc);

        txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setPreferredSize(new Dimension(200, 32));
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        card.add(txtName, gbc);

        // Contact Number
        JLabel lblContact = new JLabel("Contact Number *");
        lblContact.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContact.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        card.add(lblContact, gbc);

        txtContact = new JTextField();
        txtContact.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContact.setPreferredSize(new Dimension(200, 32));
        txtContact.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        card.add(txtContact, gbc);

        // Feedback Type
        JLabel lblType = new JLabel("Feedback Type");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblType.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        card.add(lblType, gbc);

        cbType = new JComboBox<>(new String[] {"Review", "Complaint", "Suggestion"});
        cbType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbType.setPreferredSize(new Dimension(200, 32));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        card.add(cbType, gbc);

        // Rating
        JLabel lblRating = new JLabel("Rating *");
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRating.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        card.add(lblRating, gbc);

        starPanel = new StarRatingPanel();
        starPanel.setRating(5); // Default to 5
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        card.add(starPanel, gbc);

        // Comments
        JLabel lblComments = new JLabel("Comments *");
        lblComments.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblComments.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
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
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        card.add(scrollComments, gbc);

        // Feedback Date
        JLabel lblDate = new JLabel("Feedback Date (YYYY-MM-DD) *");
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDate.setForeground(labelColor);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.3;
        card.add(lblDate, gbc);

        txtDate = new JTextField();
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDate.setPreferredSize(new Dimension(200, 32));
        // Default to current date
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        txtDate.setText(df.format(new java.util.Date()));
        txtDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        card.add(txtDate, gbc);

        // Action Buttons
        ModernButton btnSave = new ModernButton("Save Record", new Color(16, 185, 129)); // Green
        btnSave.setPreferredSize(new Dimension(130, 36));
        btnSave.addActionListener(e -> saveRecord());

        ModernButton btnCancel = new ModernButton("Cancel", new Color(100, 116, 139));
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnCancel.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        card.add(buttonPanel, gbc);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void loadFeedbackData() {
        txtName.setText(existingFeedback.getCustomerName());
        txtContact.setText(existingFeedback.getContactNumber());
        cbType.setSelectedItem(existingFeedback.getFeedbackType());
        starPanel.setRating(existingFeedback.getRating());
        taComments.setText(existingFeedback.getComments());
        txtDate.setText(existingFeedback.getFeedbackDate().toString());
    }

    private void saveRecord() {
        String name = txtName.getText();
        String contact = txtContact.getText();
        String type = (String) cbType.getSelectedItem();
        int rating = starPanel.getRating();
        String comments = taComments.getText();
        String dateStr = txtDate.getText();

        // Validate date explicitly first
        Date dateVal;
        try {
            dateVal = Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter feedback date in valid format (YYYY-MM-DD).",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success;
            if (existingFeedback == null) {
                success = controller.addFeedback(name, contact, type, rating, comments, dateVal);
            } else {
                success = controller.updateFeedback(existingFeedback.getId(), name, contact, type, rating, comments, dateVal);
            }

            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                    "Feedback record saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to save the record in database. Please check connection.",
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
}
