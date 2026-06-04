package com.feedback.model;

import java.sql.Date;

public class Feedback {
    private int id;
    private String customerName;
    private String contactNumber;
    private String feedbackType; // Complaint / Suggestion / Review
    private int rating; // 1 to 5
    private String comments;
    private Date feedbackDate;
    private String sentiment; // "Positive" / "Neutral" / "Negative" - dynamically calculated

    public Feedback() {}

    public Feedback(int id, String customerName, String contactNumber, String feedbackType, int rating, String comments, Date feedbackDate) {
        this.id = id;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.feedbackType = feedbackType;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
    }

    public Feedback(String customerName, String contactNumber, String feedbackType, int rating, String comments, Date feedbackDate) {
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.feedbackType = feedbackType;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Date getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(Date feedbackDate) { this.feedbackDate = feedbackDate; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", feedbackType='" + feedbackType + '\'' +
                ", rating=" + rating +
                ", feedbackDate=" + feedbackDate +
                '}';
    }
}
