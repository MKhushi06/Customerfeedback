package com.feedback.controller;

import com.feedback.database.AdminDAO;
import com.feedback.database.FeedbackDAO;
import com.feedback.model.AdminUser;
import com.feedback.model.Feedback;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class FeedbackController {
    private final FeedbackDAO feedbackDAO;
    private final AdminDAO adminDAO;

    // Dictionary for Sentiment Analysis
    private static final String[] POSITIVE_WORDS = {
        "good", "great", "excellent", "love", "awesome", "fantastic", "amazing", 
        "happy", "satisfied", "helpful", "friendly", "best", "perfect", "clean", 
        "fast", "speedy", "efficient", "superb", "nice", "wonderful", "glad"
    };

    private static final String[] NEGATIVE_WORDS = {
        "bad", "worst", "terrible", "hate", "awful", "horrible", "unhappy", 
        "dissatisfied", "useless", "slow", "poor", "expensive", "broken", "rude", 
        "delay", "issue", "problem", "complaint", "fail", "error", "annoying", "waste"
    };

    public FeedbackController() {
        this.feedbackDAO = new FeedbackDAO();
        this.adminDAO = new AdminDAO();
    }

    public AdminUser login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        return adminDAO.authenticate(username.trim(), password);
    }

    public String analyzeSentiment(String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            return "Neutral";
        }
        String lowerComments = comments.toLowerCase();
        int posCount = 0;
        int negCount = 0;

        for (String word : POSITIVE_WORDS) {
            if (lowerComments.contains(word)) {
                posCount++;
            }
        }
        for (String word : NEGATIVE_WORDS) {
            if (lowerComments.contains(word)) {
                negCount++;
            }
        }

        if (posCount > negCount) {
            return "Positive";
        } else if (negCount > posCount) {
            return "Negative";
        } else {
            return "Neutral";
        }
    }

    public String validateFeedbackInputs(String name, String contact, String comments, int rating) {
        if (name == null || name.trim().isEmpty()) {
            return "Customer name cannot be empty.";
        }
        if (name.trim().length() < 2) {
            return "Customer name should be at least 2 characters.";
        }
        if (contact == null || contact.trim().isEmpty()) {
            return "Contact number cannot be empty.";
        }
        // Match numbers, spaces, dashes, or parentheses; between 7 and 15 characters
        if (!contact.trim().matches("^[0-9+\\s-()]{7,15}$")) {
            return "Please enter a valid contact number (7-15 digits).";
        }
        if (rating < 1 || rating > 5) {
            return "Rating must be between 1 and 5.";
        }
        if (comments == null || comments.trim().isEmpty()) {
            return "Comments cannot be empty.";
        }
        if (comments.trim().length() > 500) {
            return "Comments must be under 500 characters.";
        }
        return null; // validation passed
    }

    public boolean addFeedback(String name, String contact, String type, int rating, String comments, Date date) {
        String validationErr = validateFeedbackInputs(name, contact, comments, rating);
        if (validationErr != null) {
            throw new IllegalArgumentException(validationErr);
        }
        Feedback f = new Feedback(name.trim(), contact.trim(), type, rating, comments.trim(), date);
        return feedbackDAO.insertFeedback(f);
    }

    public boolean updateFeedback(int id, String name, String contact, String type, int rating, String comments, Date date) {
        String validationErr = validateFeedbackInputs(name, contact, comments, rating);
        if (validationErr != null) {
            throw new IllegalArgumentException(validationErr);
        }
        Feedback f = new Feedback(id, name.trim(), contact.trim(), type, rating, comments.trim(), date);
        return feedbackDAO.updateFeedback(f);
    }

    public boolean deleteFeedback(int id) {
        return feedbackDAO.deleteFeedback(id);
    }

    public List<Feedback> getAllFeedback() {
        List<Feedback> list = feedbackDAO.getAllFeedback();
        for (Feedback f : list) {
            f.setSentiment(analyzeSentiment(f.getComments()));
        }
        return list;
    }

    public List<Feedback> searchFeedback(String nameQuery, String typeFilter, Integer ratingFilter, String dateStr) {
        List<Feedback> list = feedbackDAO.searchFeedback(nameQuery, typeFilter, ratingFilter, dateStr);
        for (Feedback f : list) {
            f.setSentiment(analyzeSentiment(f.getComments()));
        }
        return list;
    }

    public FeedbackDAO.FeedbackStats getFeedbackStats() {
        return feedbackDAO.getFeedbackStats();
    }

    public boolean exportToCSV(String filePath) {
        List<Feedback> feedbacks = getAllFeedback();
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV Header
            writer.write("ID,Customer Name,Contact Number,Feedback Type,Rating,Comments,Feedback Date,Sentiment\n");
            for (Feedback f : feedbacks) {
                writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\"\n",
                    f.getId(),
                    escapeCSV(f.getCustomerName()),
                    escapeCSV(f.getContactNumber()),
                    f.getFeedbackType(),
                    f.getRating(),
                    escapeCSV(f.getComments()),
                    f.getFeedbackDate().toString(),
                    f.getSentiment()
                ));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
