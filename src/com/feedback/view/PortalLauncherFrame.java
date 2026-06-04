package com.feedback.view;

import com.feedback.app.QRCodeGenerator;
import com.feedback.app.TunnelManager;
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
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PortalLauncherFrame extends JFrame {
    private final FeedbackController controller;

    public PortalLauncherFrame(FeedbackController controller) {
        this.controller = controller;
        setTitle("Customer Feedback Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        Color bgColor = new Color(241, 245, 249); // slate-100
        Color cardColor = Color.WHITE;
        Color primaryColor = new Color(15, 23, 42); // slate-900
        Color subColor = new Color(100, 116, 139); // slate-500

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        CardPanel card = new CardPanel(cardColor);
        card.setBorderColor(new Color(226, 232, 240));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Header Title
        JLabel lblTitle = new JLabel("Feedback Management System", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(primaryColor);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.weightx = 1.0; gbc.weighty = 0.1;
        card.add(lblTitle, gbc);

        // Subtitle
        JLabel lblSub = new JLabel("Welcome! Please select a portal to continue", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(subColor);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 8, 25, 8);
        card.add(lblSub, gbc);

        // Grid split for Customer vs Admin portal selection cards
        gbc.gridwidth = 1;
        gbc.weighty = 0.8;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Determine local IP and feedback URL
        String localIP = getLocalIPAddress();
        String feedbackUrl = "http://" + localIP + ":8085/feedback";

        // Customer Selection Card
        CardPanel customerSelect = new CardPanel(new Color(248, 250, 252));
        customerSelect.setBorderColor(new Color(226, 232, 240));
        customerSelect.setLayout(new BorderLayout(0, 8));
        customerSelect.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        JLabel lblCustTitle = new JLabel("Customer Portal", JLabel.CENTER);
        lblCustTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCustTitle.setForeground(primaryColor);
        
        // QR Code view in center
        JPanel qrContainer = new JPanel(new BorderLayout(0, 4));
        qrContainer.setOpaque(false);
        
        JLabel lblQrImage = new JLabel("", JLabel.CENTER);
        lblQrImage.setPreferredSize(new Dimension(140, 140));
        lblQrImage.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));

        lblQrImage.setText("Connecting Tunnel...");
        lblQrImage.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblQrImage.setForeground(subColor);

        JLabel lblQrLink = new JLabel("<html><center>Connecting to public server...<br>Local: " + feedbackUrl + "</center></html>", JLabel.CENTER);
        lblQrLink.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblQrLink.setForeground(subColor);

        // Local fallback in case tunnel fails or machine is offline
        new Thread(() -> {
            try {
                Thread.sleep(6000);
                if (TunnelManager.getTunnelUrl() == null) {
                    // Fall back to local Wi-Fi link
                    java.awt.EventQueue.invokeLater(() -> {
                        javax.swing.ImageIcon qrIcon = QRCodeGenerator.generateQRCode(feedbackUrl, 140, 140);
                        if (qrIcon != null) {
                            lblQrImage.setIcon(qrIcon);
                            lblQrImage.setText("");
                        } else {
                            lblQrImage.setText("QR Generation Failed");
                        }
                        lblQrLink.setText("<html><center>Scan to submit from Phone (Wi-Fi)<br><font color='#3b82f6'><b>" + feedbackUrl + "</b></font></center></html>");
                    });
                }
            } catch (InterruptedException e) {
                // Ignore
            }
        }).start();

        // Start Public SSH Tunnel
        TunnelManager.startTunnel(() -> {
            String url = TunnelManager.getTunnelUrl() + "/feedback";
            java.awt.EventQueue.invokeLater(() -> {
                javax.swing.ImageIcon qrIcon = QRCodeGenerator.generateQRCode(url, 140, 140);
                if (qrIcon != null) {
                    lblQrImage.setIcon(qrIcon);
                    lblQrImage.setText("");
                } else {
                    lblQrImage.setText("QR Generation Failed");
                }
                lblQrLink.setText("<html><center>Scan to submit from Phone (Internet)<br><font color='#3b82f6'><b>" + url + "</b></font></center></html>");
            });
        });

        qrContainer.add(lblQrImage, BorderLayout.CENTER);
        qrContainer.add(lblQrLink, BorderLayout.SOUTH);
        
        ModernButton btnCustomer = new ModernButton("Or Use Desktop Form", new Color(16, 185, 129)); // Green
        btnCustomer.addActionListener(e -> {
            setVisible(false);
            java.awt.EventQueue.invokeLater(() -> {
                CustomerFeedbackFrame custFrame = new CustomerFeedbackFrame(controller, this);
                custFrame.setVisible(true);
            });
        });
        
        customerSelect.add(lblCustTitle, BorderLayout.NORTH);
        customerSelect.add(qrContainer, BorderLayout.CENTER);
        customerSelect.add(btnCustomer, BorderLayout.SOUTH);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.5;
        card.add(customerSelect, gbc);

        // Admin Selection Card
        CardPanel adminSelect = new CardPanel(new Color(248, 250, 252));
        adminSelect.setBorderColor(new Color(226, 232, 240));
        adminSelect.setLayout(new BorderLayout(0, 10));
        adminSelect.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblAdminTitle = new JLabel("Admin Portal", JLabel.CENTER);
        lblAdminTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdminTitle.setForeground(primaryColor);
        JLabel lblAdminDesc = new JLabel("<html><center>Log in to view records, run dynamic searches, and check analytics charts.</center></html>", JLabel.CENTER);
        lblAdminDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAdminDesc.setForeground(subColor);

        ModernButton btnAdmin = new ModernButton("Administrator Log In", new Color(15, 23, 42)); // Slate dark
        btnAdmin.addActionListener(e -> {
            setVisible(false);
            java.awt.EventQueue.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(controller);
                loginFrame.setVisible(true);
                // Return control back to launcher if LoginFrame is closed, handled on exit or custom window listener
                loginFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent we) {
                        setVisible(true);
                    }
                });
            });
        });

        adminSelect.add(lblAdminTitle, BorderLayout.NORTH);
        adminSelect.add(lblAdminDesc, BorderLayout.CENTER);
        adminSelect.add(btnAdmin, BorderLayout.SOUTH);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.5;
        card.add(adminSelect, gbc);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private String getLocalIPAddress() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                java.util.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    java.net.InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address) {
                        // Skip loopback and link-local addresses (like 169.254.x.x)
                        if (addr.isLoopbackAddress() || addr.isLinkLocalAddress()) continue;
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }


}
