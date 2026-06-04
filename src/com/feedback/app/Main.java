package com.feedback.app;

import com.feedback.controller.FeedbackController;
import com.feedback.view.LoginFrame;
import com.feedback.view.PortalLauncherFrame;
import java.sql.Date;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Set Look and Feel to Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus Look and Feel not found. Using default.");
        }

        // Initialize components
        FeedbackController controller = new FeedbackController();

        // Seed dummy records if table is completely empty
        seedDataIfEmpty(controller);

        // Start Local HTTP Web Server for Mobile Feedbacks
        LocalHttpServer.start(controller);

        // Open Portal Launcher on Event Dispatch Thread (EDT)
        java.awt.EventQueue.invokeLater(() -> {
            PortalLauncherFrame launcher = new PortalLauncherFrame(controller);
            launcher.setVisible(true);
        });
    }

    private static void seedDataIfEmpty(FeedbackController controller) {
        try {
            if (controller.getAllFeedback().isEmpty()) {
                System.out.println("No records found in database. Seeding demo feedback data...");
                
                controller.addFeedback(
                    "Rohan Deshmukh",
                    "+91 98765 43210",
                    "Review",
                    5,
                    "Absolutely excellent service! The delivery was extremely fast, and the staff was friendly.",
                    Date.valueOf("2026-06-01")
                );
                
                controller.addFeedback(
                    "Priya Nair",
                    "+91 91234 56789",
                    "Complaint",
                    2,
                    "The checkout experience was slow and painful. Also, the app keeps lagging during payments.",
                    Date.valueOf("2026-06-02")
                );

                controller.addFeedback(
                    "Vikram Singh",
                    "+91 99887 76655",
                    "Suggestion",
                    4,
                    "The interface is clean, but it would be great to have dark mode and more payment options.",
                    Date.valueOf("2026-06-03")
                );

                controller.addFeedback(
                    "Ananya Sen",
                    "+91 94455 12345",
                    "Review",
                    5,
                    "Superb customer support. They resolved my query within minutes. Very satisfied!",
                    Date.valueOf("2026-06-04")
                );

                controller.addFeedback(
                    "Karthik Raju",
                    "+91 95556 66777",
                    "Complaint",
                    1,
                    "Terrible product quality. The package was damaged and the customer helpline is useless and rude.",
                    Date.valueOf("2026-06-04")
                );
                
                System.out.println("Seeding completed successfully.");
            }
        } catch (Exception e) {
            System.err.println("Failed to seed dummy data: " + e.getMessage());
        }
    }
}
