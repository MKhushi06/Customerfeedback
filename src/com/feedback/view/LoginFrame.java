package com.feedback.view;

import com.feedback.controller.FeedbackController;
import com.feedback.model.AdminUser;
import com.feedback.view.components.CardPanel;
import com.feedback.view.components.ModernButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class LoginFrame extends JFrame {
    private final FeedbackController controller;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame(FeedbackController controller) {
        this.controller = controller;
        setTitle("Customer Feedback Manager - Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(null);
        setResizable(false);

        // Setup custom styles
        initUI();
    }

    private void initUI() {
        // Deep blue/slate theme backgrounds
        Color bgColor = new Color(241, 245, 249); // slate-100
        Color cardColor = Color.WHITE;
        Color primaryColor = new Color(15, 23, 42); // slate-900

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        CardPanel card = new CardPanel(cardColor);
        card.setBorderColor(new Color(226, 232, 240));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Title
        JLabel lblTitle = new JLabel("Admin Login", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(primaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        card.add(lblTitle, gbc);

        // Subtitle
        JLabel lblSub = new JLabel("Enter your credentials to access dashboard", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 8, 20, 8);
        card.add(lblSub, gbc);

        // Username Label
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(71, 85, 105));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(6, 8, 2, 8);
        card.add(lblUser, gbc);

        // Username Field
        txtUsername = new JTextField("admin"); // Pre-fill default username
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(200, 32));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        card.add(txtUsername, gbc);

        // Password Label
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(71, 85, 105));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(6, 8, 2, 8);
        card.add(lblPass, gbc);

        // Password Field
        txtPassword = new JPasswordField("admin123"); // Pre-fill default password
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(200, 32));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        card.add(txtPassword, gbc);

        // Login Button
        ModernButton btnLogin = new ModernButton("Log In", new Color(15, 23, 42));
        btnLogin.setPreferredSize(new Dimension(130, 36));
        btnLogin.addActionListener(e -> performLogin());

        // Cancel/Exit Button
        ModernButton btnExit = new ModernButton("Exit", new Color(100, 116, 139));
        btnExit.setPreferredSize(new Dimension(100, 36));
        btnExit.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        card.add(buttonPanel, gbc);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);

        // Bind Enter key to trigger login
        txtPassword.addActionListener(e -> performLogin());
        txtUsername.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        AdminUser user = controller.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                "Welcome, " + user.getFullName() + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open Main Dashboard
            dispose();
            java.awt.EventQueue.invokeLater(() -> {
                MainDashboard dashboard = new MainDashboard(controller, user);
                dashboard.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password. Please try again.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
