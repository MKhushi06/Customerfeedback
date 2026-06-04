package com.feedback.view.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class ModernButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color clickColor;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text, Color baseColor) {
        super(text);
        this.normalColor = baseColor;
        // Derive hover and click colors dynamically
        this.hoverColor = brighten(baseColor, 0.15);
        this.clickColor = darken(baseColor, 0.15);

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 36));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    private Color brighten(Color color, double fraction) {
        int r = Math.min(255, (int) (color.getRed() * (1 + fraction)));
        int g = Math.min(255, (int) (color.getGreen() * (1 + fraction)));
        int b = Math.min(255, (int) (color.getBlue() * (1 + fraction)));
        return new Color(r, g, b);
    }

    private Color darken(Color color, double fraction) {
        int r = Math.max(0, (int) (color.getRed() * (1 - fraction)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - fraction)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - fraction)));
        return new Color(r, g, b);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg;
        if (isPressed) {
            bg = clickColor;
        } else if (isHovered) {
            bg = hoverColor;
        } else {
            bg = normalColor;
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        super.paintComponent(g2);
        g2.dispose();
    }
}
