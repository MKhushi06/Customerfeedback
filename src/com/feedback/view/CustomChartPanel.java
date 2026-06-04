package com.feedback.view;

import com.feedback.database.FeedbackDAO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

public class CustomChartPanel extends JPanel {
    private FeedbackDAO.FeedbackStats stats;
    // Map rating frequencies
    private int[] ratingCounts = new int[5]; // index 0=1 star, 4=5 stars

    private static final Color COLOR_BAR = new Color(59, 130, 246); // Blue
    private static final Color COLOR_BG = new Color(255, 255, 255);
    private static final Color COLOR_TEXT = new Color(71, 85, 105); // Slate
    private static final Color COLOR_GRID = new Color(241, 245, 249);

    private static final Color COLOR_COMPLAINT = new Color(239, 68, 68); // Red
    private static final Color COLOR_SUGGESTION = new Color(59, 130, 246); // Blue
    private static final Color COLOR_REVIEW = new Color(16, 185, 129); // Green
    private static final Color COLOR_EMPTY = new Color(226, 232, 240); // Grey

    public CustomChartPanel() {
        setBackground(COLOR_BG);
        setOpaque(true);
    }

    public void updateData(FeedbackDAO.FeedbackStats stats, int[] ratingCounts) {
        this.stats = stats;
        if (ratingCounts != null) {
            this.ratingCounts = ratingCounts;
        } else {
            this.ratingCounts = new int[5];
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (stats == null || stats.totalCount == 0) {
            drawEmptyState(g2, w, h);
            g2.dispose();
            return;
        }

        // Split panel into two charts: Bar Chart on Left, Donut Chart on Right
        int halfW = w / 2;

        drawBarChart(g2, 10, 10, halfW - 20, h - 20);
        drawDonutChart(g2, halfW + 10, 10, halfW - 20, h - 20);

        g2.dispose();
    }

    private void drawEmptyState(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(COLOR_TEXT);
        String msg = "No Feedback Data Available to Display Charts";
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(msg)) / 2;
        int y = h / 2;
        g2.drawString(msg, x, y);
    }

    private void drawBarChart(Graphics2D g2, int x, int y, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(COLOR_TEXT);
        g2.drawString("Ratings Distribution", x + 10, y + 15);

        int chartX = x + 30;
        int chartY = y + 35;
        int chartW = w - 40;
        int chartH = h - 60;

        // Draw horizontal grid lines
        g2.setColor(COLOR_GRID);
        for (int i = 0; i <= 4; i++) {
            int gridY = chartY + chartH - (i * chartH / 4);
            g2.drawLine(chartX, gridY, chartX + chartW, gridY);
        }

        // Find max frequency
        int maxVal = 0;
        for (int count : ratingCounts) {
            if (count > maxVal) maxVal = count;
        }
        if (maxVal == 0) maxVal = 1;

        // Draw Bars
        int numBars = 5;
        int barGap = 15;
        int barW = (chartW - (barGap * (numBars + 1))) / numBars;

        for (int i = 0; i < numBars; i++) {
            int ratingVal = ratingCounts[i];
            int barH = (ratingVal * chartH) / maxVal;
            int barX = chartX + barGap + i * (barW + barGap);
            int barY = chartY + chartH - barH;

            // Draw Bar
            g2.setColor(COLOR_BAR);
            if (barH > 0) {
                // Draw rounded top rectangle
                g2.fill(new RoundRectangle2D.Double(barX, barY, barW, barH, 8, 8));
                // Overlay a flat bottom to make only top rounded
                g2.fillRect(barX, barY + Math.min(barH, 4), barW, Math.max(0, barH - 4));
            }

            // Draw value above bar
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(COLOR_TEXT);
            String valStr = String.valueOf(ratingVal);
            int valW = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, barX + (barW - valW) / 2, barY - 4);

            // Draw Label (1-5 Star) below bar
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String label = (i + 1) + "★";
            int labelW = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, barX + (barW - labelW) / 2, chartY + chartH + 18);
        }
    }

    private void drawDonutChart(Graphics2D g2, int x, int y, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(COLOR_TEXT);
        g2.drawString("Feedback Categories", x + 10, y + 15);

        int total = stats.totalCount;
        if (total == 0) return;

        double compPct = (double) stats.complaintCount / total;
        double sugPct = (double) stats.suggestionCount / total;
        double revPct = (double) stats.reviewCount / total;

        int size = Math.min(w - 120, h - 60);
        int cx = x + 15;
        int cy = y + 40;

        // Draw Arcs
        double currentAngle = 0;

        // Complaint Arc
        double compAngle = compPct * 360;
        if (compAngle > 0) {
            g2.setColor(COLOR_COMPLAINT);
            g2.fill(new Arc2D.Double(cx, cy, size, size, currentAngle, compAngle, Arc2D.PIE));
            currentAngle += compAngle;
        }

        // Suggestion Arc
        double sugAngle = sugPct * 360;
        if (sugAngle > 0) {
            g2.setColor(COLOR_SUGGESTION);
            g2.fill(new Arc2D.Double(cx, cy, size, size, currentAngle, sugAngle, Arc2D.PIE));
            currentAngle += sugAngle;
        }

        // Review Arc
        double revAngle = revPct * 360;
        if (revAngle > 0) {
            g2.setColor(COLOR_REVIEW);
            g2.fill(new Arc2D.Double(cx, cy, size, size, currentAngle, revAngle, Arc2D.PIE));
            currentAngle += revAngle;
        }

        // Inner circle for donut look
        int donutSize = size / 2;
        int dcx = cx + (size - donutSize) / 2;
        int dcy = cy + (size - donutSize) / 2;
        g2.setColor(COLOR_BG);
        g2.fillOval(dcx, dcy, donutSize, donutSize);

        // Draw center text
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(COLOR_TEXT);
        String label = "Total: " + total;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, dcx + (donutSize - fm.stringWidth(label)) / 2, dcy + (donutSize + fm.getAscent()) / 2 - 2);

        // Draw Legend
        int legendX = x + size + 25;
        int legendY = cy + 20;
        int rectSize = 10;
        int lineH = 20;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Complaint Legend
        g2.setColor(COLOR_COMPLAINT);
        g2.fillRect(legendX, legendY, rectSize, rectSize);
        g2.setColor(COLOR_TEXT);
        g2.drawString(String.format("Complaints: %d (%.0f%%)", stats.complaintCount, compPct * 100), legendX + 15, legendY + 9);

        // Suggestion Legend
        legendY += lineH;
        g2.setColor(COLOR_SUGGESTION);
        g2.fillRect(legendX, legendY, rectSize, rectSize);
        g2.setColor(COLOR_TEXT);
        g2.drawString(String.format("Suggestions: %d (%.0f%%)", stats.suggestionCount, sugPct * 100), legendX + 15, legendY + 9);

        // Review Legend
        legendY += lineH;
        g2.setColor(COLOR_REVIEW);
        g2.fillRect(legendX, legendY, rectSize, rectSize);
        g2.setColor(COLOR_TEXT);
        g2.drawString(String.format("Reviews: %d (%.0f%%)", stats.reviewCount, revPct * 100), legendX + 15, legendY + 9);
    }
}
