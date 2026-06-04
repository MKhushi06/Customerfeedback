package com.feedback.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import javax.swing.JPanel;

public class StarRatingPanel extends JPanel {
    private int rating = 3;
    private int hoverRating = 0;
    private boolean editable = true;
    private static final int STAR_SIZE = 24;
    private static final int GAP = 6;
    private static final Color GOLD = new Color(245, 158, 11);
    private static final Color GRAY = new Color(209, 213, 219);

    public StarRatingPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension((STAR_SIZE + GAP) * 5 - GAP, STAR_SIZE + 4));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!editable) return;
                int clickedIndex = getStarIndexAt(e.getX());
                if (clickedIndex != hoverRating) {
                    hoverRating = clickedIndex;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!editable) return;
                hoverRating = 0;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!editable) return;
                int clickedIndex = getStarIndexAt(e.getX());
                if (clickedIndex > 0 && clickedIndex <= 5) {
                    setRating(clickedIndex);
                    // Fire property change event to notify listeners
                    firePropertyChange("rating", null, rating);
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
        repaint();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        setCursor(editable ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
    }

    private int getStarIndexAt(int x) {
        for (int i = 0; i < 5; i++) {
            int starX = i * (STAR_SIZE + GAP);
            if (x >= starX && x <= starX + STAR_SIZE) {
                return i + 1;
            }
        }
        return 0;
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

        int activeLimit = (hoverRating > 0) ? hoverRating : rating;

        for (int i = 0; i < 5; i++) {
            int starX = i * (STAR_SIZE + GAP);
            // Center of the star
            double cx = starX + STAR_SIZE / 2.0;
            double cy = STAR_SIZE / 2.0 + 2.0;
            
            Path2D star = createStar(cx, cy, STAR_SIZE / 4.5, STAR_SIZE / 2.0);

            if (i < activeLimit) {
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
