package com.feedback.view;

import com.feedback.controller.FeedbackController;
import com.feedback.database.FeedbackDAO;
import com.feedback.model.AdminUser;
import com.feedback.model.Feedback;
import com.feedback.view.components.CardPanel;
import com.feedback.view.components.ModernButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

public class MainDashboard extends JFrame {
    private final FeedbackController controller;
    private final AdminUser adminUser;

    // GUI components
    private JTable tblRecords;
    private DefaultTableModel tableModel;
    
    // Stats labels
    private JLabel lblAvgRating;
    private JLabel lblTotalCount;
    private JLabel lblComplaintCount;
    private JLabel lblNegativeCount;

    // Detail View Components
    private JLabel lblDetailName;
    private JLabel lblDetailContact;
    private JLabel lblDetailType;
    private StarRatingPanel detailStarPanel;
    private JLabel lblDetailDate;
    private JLabel lblDetailSentiment;
    private JTextArea taDetailComments;
    private CardPanel detailsCard;

    // Search and filters
    private JTextField txtSearch;
    private JComboBox<String> cbFilterType;
    private JComboBox<String> cbFilterRating;
    private JTextField txtFilterDate;

    // Custom Chart Panel
    private CustomChartPanel chartPanel;

    // Theme Colors
    private static final Color COLOR_BG = new Color(248, 250, 252); // slate-50
    private static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42); // slate-900
    private static final Color COLOR_TEXT_SUB = new Color(100, 116, 139); // slate-500
    private static final Color COLOR_BORDER = new Color(226, 232, 240); // slate-200

    public MainDashboard(FeedbackController controller, AdminUser adminUser) {
        this.controller = controller;
        this.adminUser = adminUser;

        setTitle("Customer Feedback Management Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        initUI();
        refreshDashboardData();
    }

    private void initUI() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(COLOR_BG);
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Header (User profile and Statistics)
        container.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Middle workspace: split into Left (Table & Controls) and Right (Visual Charts)
        JPanel workspacePanel = new JPanel(new BorderLayout(15, 0));
        workspacePanel.setOpaque(false);

        // Left Panel (Search, Filters, JTable and Actions)
        workspacePanel.add(createLeftPanel(), BorderLayout.CENTER);

        // Right Panel (Visual Analytics Charts)
        workspacePanel.add(createRightPanel(), BorderLayout.EAST);

        container.add(workspacePanel, BorderLayout.CENTER);
        add(container);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Welcome / Admin Profile row
        JPanel profileRow = new JPanel(new BorderLayout());
        profileRow.setOpaque(false);

        JLabel lblWelcome = new JLabel("Welcome back, " + adminUser.getFullName() + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(COLOR_TEXT_MAIN);

        JLabel lblRole = new JLabel("Role: System Administrator | Database: Connected");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(16, 185, 129)); // Green indicator

        JPanel profileDetails = new JPanel();
        profileDetails.setOpaque(false);
        profileDetails.setLayout(new BoxLayout(profileDetails, BoxLayout.Y_AXIS));
        profileDetails.add(lblWelcome);
        profileDetails.add(Box.createRigidArea(new Dimension(0, 2)));
        profileDetails.add(lblRole);

        profileRow.add(profileDetails, BorderLayout.WEST);

        // Logout Button
        ModernButton btnLogout = new ModernButton("Log Out", new Color(239, 68, 68));
        btnLogout.setPreferredSize(new Dimension(100, 32));
        btnLogout.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                dispose();
                java.awt.EventQueue.invokeLater(() -> new LoginFrame(controller).setVisible(true));
            }
        });
        profileRow.add(btnLogout, BorderLayout.EAST);

        headerPanel.add(profileRow, BorderLayout.NORTH);

        // Statistics Cards Panel
        JPanel cardsGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        cardsGrid.setOpaque(false);

        // Card 1: Average Rating
        CardPanel cardAvg = new CardPanel(Color.WHITE);
        cardAvg.setPreferredSize(new Dimension(200, 80));
        cardAvg.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        cardAvg.setLayout(new GridLayout(2, 1));
        JLabel lblAvgTitle = new JLabel("Average Rating", JLabel.LEFT);
        lblAvgTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAvgTitle.setForeground(COLOR_TEXT_SUB);
        lblAvgRating = new JLabel("N/A", JLabel.LEFT);
        lblAvgRating.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblAvgRating.setForeground(new Color(245, 158, 11)); // Amber
        cardAvg.add(lblAvgTitle);
        cardAvg.add(lblAvgRating);
        cardsGrid.add(cardAvg);

        // Card 2: Total Feedback Records
        CardPanel cardTotal = new CardPanel(Color.WHITE);
        cardTotal.setPreferredSize(new Dimension(200, 80));
        cardTotal.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        cardTotal.setLayout(new GridLayout(2, 1));
        JLabel lblTotalTitle = new JLabel("Total Feedback", JLabel.LEFT);
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalTitle.setForeground(COLOR_TEXT_SUB);
        lblTotalCount = new JLabel("0", JLabel.LEFT);
        lblTotalCount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalCount.setForeground(new Color(59, 130, 246)); // Blue
        cardTotal.add(lblTotalTitle);
        cardTotal.add(lblTotalCount);
        cardsGrid.add(cardTotal);

        // Card 3: Complaints
        CardPanel cardComplaints = new CardPanel(Color.WHITE);
        cardComplaints.setPreferredSize(new Dimension(200, 80));
        cardComplaints.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        cardComplaints.setLayout(new GridLayout(2, 1));
        JLabel lblComplTitle = new JLabel("Complaints", JLabel.LEFT);
        lblComplTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblComplTitle.setForeground(COLOR_TEXT_SUB);
        lblComplaintCount = new JLabel("0", JLabel.LEFT);
        lblComplaintCount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblComplaintCount.setForeground(new Color(139, 92, 246)); // Purple
        cardComplaints.add(lblComplTitle);
        cardComplaints.add(lblComplaintCount);
        cardsGrid.add(cardComplaints);

        // Card 4: Negative Feedbacks Highlighted
        CardPanel cardNegative = new CardPanel(new Color(254, 242, 242)); // light red
        cardNegative.setBorderColor(new Color(252, 165, 165));
        cardNegative.setPreferredSize(new Dimension(200, 80));
        cardNegative.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        cardNegative.setLayout(new GridLayout(2, 1));
        JLabel lblNegTitle = new JLabel("Negative Feedback (1-2★)", JLabel.LEFT);
        lblNegTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNegTitle.setForeground(new Color(220, 38, 38)); // Dark red
        lblNegativeCount = new JLabel("0", JLabel.LEFT);
        lblNegativeCount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNegativeCount.setForeground(new Color(220, 38, 38));
        cardNegative.add(lblNegTitle);
        cardNegative.add(lblNegativeCount);
        cardsGrid.add(cardNegative);

        headerPanel.add(cardsGrid, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);

        // Search & Filters Card
        CardPanel filterCard = new CardPanel(Color.WHITE);
        filterCard.setBorderColor(COLOR_BORDER);
        filterCard.setLayout(new GridBagLayout());
        filterCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);

        // Search Bar
        JLabel lblSearch = new JLabel("Search by Name");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSearch.setForeground(COLOR_TEXT_SUB);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        filterCard.add(lblSearch, gbc);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        filterCard.add(txtSearch, gbc);

        // Filter by Feedback Type
        JLabel lblFilterType = new JLabel("Feedback Type");
        lblFilterType.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFilterType.setForeground(COLOR_TEXT_SUB);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        filterCard.add(lblFilterType, gbc);

        cbFilterType = new JComboBox<>(new String[]{"All", "Review", "Complaint", "Suggestion"});
        cbFilterType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        filterCard.add(cbFilterType, gbc);

        // Filter by Rating
        JLabel lblFilterRating = new JLabel("Rating Filter");
        lblFilterRating.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFilterRating.setForeground(COLOR_TEXT_SUB);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.15;
        filterCard.add(lblFilterRating, gbc);

        cbFilterRating = new JComboBox<>(new String[]{"All", "5★", "4★", "3★", "2★", "1★"});
        cbFilterRating.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.15;
        filterCard.add(cbFilterRating, gbc);

        // Filter by Date
        JLabel lblFilterDate = new JLabel("Date (YYYY-MM-DD)");
        lblFilterDate.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFilterDate.setForeground(COLOR_TEXT_SUB);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        filterCard.add(lblFilterDate, gbc);

        txtFilterDate = new JTextField();
        txtFilterDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFilterDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        filterCard.add(txtFilterDate, gbc);

        // Search & Reset Buttons
        ModernButton btnSearch = new ModernButton("Search", new Color(59, 130, 246));
        btnSearch.setPreferredSize(new Dimension(85, 30));
        btnSearch.addActionListener(e -> performSearch());

        ModernButton btnClearFilters = new ModernButton("Reset", new Color(148, 163, 184));
        btnClearFilters.setPreferredSize(new Dimension(75, 30));
        btnClearFilters.addActionListener(e -> clearFilters());

        JPanel actionBtnGrid = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionBtnGrid.setOpaque(false);
        actionBtnGrid.add(btnSearch);
        actionBtnGrid.add(btnClearFilters);

        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 0.15;
        filterCard.add(actionBtnGrid, gbc);

        leftPanel.add(filterCard, BorderLayout.NORTH);

        // Table Panel Card
        CardPanel tableCard = new CardPanel(Color.WHITE);
        tableCard.setBorderColor(COLOR_BORDER);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table columns
        String[] columns = {"ID", "Customer Name", "Contact", "Type", "Rating", "Comments", "Date", "Sentiment"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only cells
            }
        };

        tblRecords = new JTable(tableModel);
        tblRecords.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblRecords.setRowHeight(32);
        tblRecords.setGridColor(new Color(241, 245, 249));
        tblRecords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRecords.setSelectionBackground(new Color(239, 246, 255)); // Light blue select
        tblRecords.setSelectionForeground(COLOR_TEXT_MAIN);

        // Style header
        JTableHeader header = tblRecords.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(COLOR_TEXT_MAIN);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        // Apply custom renderers
        tblRecords.getColumnModel().getColumn(3).setCellRenderer(new FeedbackTypeRenderer()); // Type styling
        tblRecords.getColumnModel().getColumn(4).setCellRenderer(new RatingStarsRenderer()); // Rating stars
        tblRecords.getColumnModel().getColumn(7).setCellRenderer(new SentimentRenderer()); // Sentiment badge

        tblRecords.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsSection();
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblRecords);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollTable, BorderLayout.CENTER);

        // Bottom Actions Bar inside Left Panel
        JPanel bottomActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bottomActions.setOpaque(false);
        bottomActions.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        ModernButton btnAdd = new ModernButton("Add Feedback", new Color(16, 185, 129)); // Green
        btnAdd.addActionListener(e -> openAddDialog());

        ModernButton btnEdit = new ModernButton("Edit Selected", new Color(245, 158, 11)); // Amber
        btnEdit.addActionListener(e -> openEditDialog());

        ModernButton btnDelete = new ModernButton("Delete", new Color(239, 68, 68)); // Red
        btnDelete.addActionListener(e -> deleteSelectedRecord());

        ModernButton btnExport = new ModernButton("Export CSV", new Color(15, 23, 42)); // Slate dark
        btnExport.addActionListener(e -> exportRecordsToCSV());

        ModernButton btnRefresh = new ModernButton("Refresh Table", new Color(59, 130, 246)); // Blue
        btnRefresh.addActionListener(e -> refreshDashboardData());

        bottomActions.add(btnAdd);
        bottomActions.add(btnEdit);
        bottomActions.add(btnDelete);
        bottomActions.add(btnExport);
        bottomActions.add(btnRefresh);

        // Create Details Card
        detailsCard = new CardPanel(new Color(248, 250, 252)); // slate-50 background for details area
        detailsCard.setBorderColor(COLOR_BORDER);
        detailsCard.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        detailsCard.setLayout(new BorderLayout(15, 0));
        detailsCard.setPreferredSize(new Dimension(500, 125));

        // Left side of details: Info grid
        JPanel infoGrid = new JPanel(new GridBagLayout());
        infoGrid.setOpaque(false);
        GridBagConstraints dGbc = new GridBagConstraints();
        dGbc.fill = GridBagConstraints.HORIZONTAL;
        dGbc.insets = new Insets(1, 0, 1, 8);

        JLabel lblDetailsHeader = new JLabel("FEEDBACK DETAIL VIEW");
        lblDetailsHeader.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblDetailsHeader.setForeground(COLOR_TEXT_SUB);
        dGbc.gridx = 0; dGbc.gridy = 0; dGbc.gridwidth = 2;
        infoGrid.add(lblDetailsHeader, dGbc);

        dGbc.gridwidth = 1;

        lblDetailName = new JLabel("No Selection");
        lblDetailName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetailName.setForeground(COLOR_TEXT_MAIN);
        dGbc.gridx = 0; dGbc.gridy = 1; dGbc.gridwidth = 2;
        infoGrid.add(lblDetailName, dGbc);

        dGbc.gridwidth = 1;

        lblDetailContact = new JLabel("");
        lblDetailContact.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDetailContact.setForeground(COLOR_TEXT_SUB);
        dGbc.gridx = 0; dGbc.gridy = 2;
        infoGrid.add(lblDetailContact, dGbc);

        lblDetailDate = new JLabel("");
        lblDetailDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDetailDate.setForeground(COLOR_TEXT_SUB);
        dGbc.gridx = 1; dGbc.gridy = 2;
        infoGrid.add(lblDetailDate, dGbc);

        lblDetailType = new JLabel("");
        lblDetailType.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dGbc.gridx = 0; dGbc.gridy = 3;
        infoGrid.add(lblDetailType, dGbc);

        lblDetailSentiment = new JLabel("");
        lblDetailSentiment.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dGbc.gridx = 1; dGbc.gridy = 3;
        infoGrid.add(lblDetailSentiment, dGbc);

        JLabel lblRatingTitle = new JLabel("Rating: ");
        lblRatingTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRatingTitle.setForeground(COLOR_TEXT_SUB);
        dGbc.gridx = 0; dGbc.gridy = 4;
        infoGrid.add(lblRatingTitle, dGbc);

        detailStarPanel = new StarRatingPanel();
        detailStarPanel.setRating(0);
        detailStarPanel.setEditable(false);
        detailStarPanel.setPreferredSize(new Dimension(144, 28));
        dGbc.gridx = 1; dGbc.gridy = 4;
        infoGrid.add(detailStarPanel, dGbc);

        detailsCard.add(infoGrid, BorderLayout.WEST);

        // Right side of details: Comments JTextArea inside ScrollPane
        taDetailComments = new JTextArea("Select a feedback record from the table above to view the comments and analysis here.");
        taDetailComments.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        taDetailComments.setForeground(COLOR_TEXT_SUB);
        taDetailComments.setLineWrap(true);
        taDetailComments.setWrapStyleWord(true);
        taDetailComments.setEditable(false);
        taDetailComments.setOpaque(false);
        taDetailComments.setBackground(new Color(0, 0, 0, 0));

        JScrollPane scrollDetailComments = new JScrollPane(taDetailComments);
        scrollDetailComments.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        scrollDetailComments.getViewport().setOpaque(false);
        scrollDetailComments.setOpaque(false);
        detailsCard.add(scrollDetailComments, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel();
        bottomContainer.setOpaque(false);
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.add(detailsCard);
        bottomContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomContainer.add(bottomActions);

        tableCard.add(bottomContainer, BorderLayout.SOUTH);

        leftPanel.add(tableCard, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        CardPanel rightPanel = new CardPanel(Color.WHITE);
        rightPanel.setBorderColor(COLOR_BORDER);
        rightPanel.setPreferredSize(new Dimension(460, 500));
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblAnalyticsTitle = new JLabel("Live Analytics & Charts");
        lblAnalyticsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAnalyticsTitle.setForeground(COLOR_TEXT_MAIN);
        lblAnalyticsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        rightPanel.add(lblAnalyticsTitle, BorderLayout.NORTH);

        chartPanel = new CustomChartPanel();
        rightPanel.add(chartPanel, BorderLayout.CENTER);

        return rightPanel;
    }

    private void refreshDashboardData() {
        performSearch();
    }

    private void performSearch() {
        String searchQuery = txtSearch.getText();
        String typeFilter = (String) cbFilterType.getSelectedItem();
        String ratingFilterStr = (String) cbFilterRating.getSelectedItem();
        String dateFilter = txtFilterDate.getText();

        Integer ratingVal = null;
        if (ratingFilterStr != null && !ratingFilterStr.equals("All")) {
            ratingVal = Character.getNumericValue(ratingFilterStr.charAt(0));
        }

        List<Feedback> list = controller.searchFeedback(searchQuery, typeFilter, ratingVal, dateFilter);

        // Populate Table Model
        tableModel.setRowCount(0);
        int[] rCounts = new int[5]; // Count rating 1 to 5

        for (Feedback f : list) {
            tableModel.addRow(new Object[]{
                f.getId(),
                f.getCustomerName(),
                f.getContactNumber(),
                f.getFeedbackType(),
                f.getRating(),
                f.getComments(),
                f.getFeedbackDate(),
                f.getSentiment()
            });

            // Update rating frequencies for the visible items
            int r = f.getRating();
            if (r >= 1 && r <= 5) {
                rCounts[r - 1]++;
            }
        }

        // Fetch Overall Statistics from database (DAO stats computes stats on the entire table)
        FeedbackDAO.FeedbackStats stats = controller.getFeedbackStats();

        // Update Stats Cards
        if (stats.totalCount > 0) {
            lblAvgRating.setText(String.format("%.2f ★", stats.averageRating));
            lblTotalCount.setText(String.valueOf(stats.totalCount));
            lblComplaintCount.setText(String.valueOf(stats.complaintCount));
            lblNegativeCount.setText(String.valueOf(stats.negativeCount));
        } else {
            lblAvgRating.setText("N/A");
            lblTotalCount.setText("0");
            lblComplaintCount.setText("0");
            lblNegativeCount.setText("0");
        }

        // Refresh Chart Panel
        chartPanel.updateData(stats, rCounts);
        updateDetailsSection();
    }

    private void clearFilters() {
        txtSearch.setText("");
        cbFilterType.setSelectedIndex(0);
        cbFilterRating.setSelectedIndex(0);
        txtFilterDate.setText("");
        refreshDashboardData();
    }

    private void openAddDialog() {
        FeedbackFormDialog dialog = new FeedbackFormDialog(this, controller, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshDashboardData();
        }
    }

    private void openEditDialog() {
        int selectedRow = tblRecords.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a feedback record from the table to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblRecords.getValueAt(selectedRow, 0);
        String name = (String) tblRecords.getValueAt(selectedRow, 1);
        String contact = (String) tblRecords.getValueAt(selectedRow, 2);
        String type = (String) tblRecords.getValueAt(selectedRow, 3);
        int rating = (int) tblRecords.getValueAt(selectedRow, 4);
        String comments = (String) tblRecords.getValueAt(selectedRow, 5);
        java.sql.Date date = (java.sql.Date) tblRecords.getValueAt(selectedRow, 6);

        Feedback f = new Feedback(id, name, contact, type, rating, comments, date);

        FeedbackFormDialog dialog = new FeedbackFormDialog(this, controller, f);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshDashboardData();
        }
    }

    private void deleteSelectedRecord() {
        int selectedRow = tblRecords.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a feedback record from the table to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblRecords.getValueAt(selectedRow, 0);
        String customerName = (String) tblRecords.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete feedback from " + customerName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteFeedback(id);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Record deleted successfully.",
                    "Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshDashboardData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete record. Please check database connection.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportRecordsToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Feedback Data to CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File("customer_feedback_report.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                path += ".csv";
            }
            
            boolean success = controller.exportToCSV(path);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Report successfully exported to:\n" + path,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "An error occurred while exporting report to CSV.",
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDetailsSection() {
        int selectedRow = tblRecords.getSelectedRow();
        if (detailsCard == null) return;
        if (selectedRow < 0) {
            lblDetailName.setText("No Selection");
            lblDetailContact.setText("");
            lblDetailDate.setText("");
            lblDetailType.setText("");
            lblDetailSentiment.setText("");
            detailStarPanel.setRating(0);
            taDetailComments.setText("Select a feedback record from the table above to view the comments and analysis here.");
            taDetailComments.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            taDetailComments.setForeground(COLOR_TEXT_SUB);
            return;
        }

        String name = (String) tblRecords.getValueAt(selectedRow, 1);
        String contact = (String) tblRecords.getValueAt(selectedRow, 2);
        String type = (String) tblRecords.getValueAt(selectedRow, 3);
        int rating = (int) tblRecords.getValueAt(selectedRow, 4);
        String comments = (String) tblRecords.getValueAt(selectedRow, 5);
        java.sql.Date date = (java.sql.Date) tblRecords.getValueAt(selectedRow, 6);
        String sentiment = (String) tblRecords.getValueAt(selectedRow, 7);

        lblDetailName.setText(name);
        lblDetailContact.setText("📞 " + contact);
        lblDetailDate.setText("📅 " + date.toString());
        
        lblDetailType.setText(type.toUpperCase());
        if (type.equals("Complaint")) {
            lblDetailType.setForeground(new Color(220, 38, 38));
        } else if (type.equals("Suggestion")) {
            lblDetailType.setForeground(new Color(37, 99, 235));
        } else {
            lblDetailType.setForeground(new Color(5, 150, 105));
        }

        lblDetailSentiment.setText(sentiment.toUpperCase());
        if (sentiment.equals("Positive")) {
            lblDetailSentiment.setForeground(new Color(16, 185, 129));
        } else if (sentiment.equals("Negative")) {
            lblDetailSentiment.setForeground(new Color(239, 68, 68));
        } else {
            lblDetailSentiment.setForeground(new Color(100, 116, 139));
        }

        detailStarPanel.setRating(rating);
        taDetailComments.setText(comments);
        taDetailComments.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        taDetailComments.setForeground(COLOR_TEXT_MAIN);
    }

    // CUSTOM JTABLE RENDERERS FOR STRIKING PREMIUM LOOKS

    private static class FeedbackTypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setHorizontalAlignment(JLabel.CENTER);

            if (value != null) {
                String val = (String) value;
                if (val.equals("Complaint")) {
                    label.setForeground(new Color(220, 38, 38)); // Crimson
                } else if (val.equals("Suggestion")) {
                    label.setForeground(new Color(37, 99, 235)); // Royal Blue
                } else if (val.equals("Review")) {
                    label.setForeground(new Color(5, 150, 105)); // Emerald Green
                }
            }
            return label;
        }
    }

    private static class RatingStarsRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private int rating = 0;
        private static final int STAR_SIZE = 14;
        private static final int GAP = 4;
        private static final Color GOLD = new Color(245, 158, 11);
        private static final Color GRAY = new Color(209, 213, 219); // light gray for outline

        public RatingStarsRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Integer) {
                this.rating = (Integer) value;
            } else {
                this.rating = 0;
            }
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }

        private Path2D createStar(double x, double y, double innerRadius, double outerRadius) {
            Path2D path = new Path2D.Double();
            int numRays = 5;
            double angle = Math.PI / numRays;
            for (int i = 0; i < 2 * numRays; i++) {
                double r = (i % 2 == 0) ? outerRadius : innerRadius;
                double a = i * angle - Math.PI / 2;
                double px = x + r * Math.cos(a);
                double py = y + r * Math.sin(a);
                if (i == 0) {
                    path.moveTo(px, py);
                } else {
                    path.lineTo(px, py);
                }
            }
            path.closePath();
            return path;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int totalWidth = (STAR_SIZE + GAP) * 5 - GAP;
            int startX = (width - totalWidth) / 2;

            for (int i = 0; i < 5; i++) {
                int starX = startX + i * (STAR_SIZE + GAP);
                double cx = starX + STAR_SIZE / 2.0;
                double cy = height / 2.0;
                
                Path2D star = createStar(cx, cy, STAR_SIZE / 4.5, STAR_SIZE / 2.0);

                if (i < rating) {
                    g2.setColor(GOLD);
                    g2.fill(star);
                } else {
                    g2.setColor(GRAY);
                    g2.draw(star);
                }
            }

            g2.dispose();
        }
    }

    private static class SentimentRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setHorizontalAlignment(JLabel.CENTER);

            if (value != null) {
                String sentiment = (String) value;
                if (sentiment.equals("Positive")) {
                    label.setForeground(new Color(16, 185, 129)); // Green
                    label.setText("🟢 Positive");
                } else if (sentiment.equals("Negative")) {
                    label.setForeground(new Color(239, 68, 68)); // Red
                    label.setText("🔴 Negative");
                } else {
                    label.setForeground(new Color(100, 116, 139)); // Grey
                    label.setText("⚪ Neutral");
                }
            }
            return label;
        }
    }
}
