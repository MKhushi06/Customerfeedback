package com.feedback.database;

import com.feedback.model.Feedback;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    public static class FeedbackStats {
        public double averageRating;
        public int totalCount;
        public int complaintCount;
        public int suggestionCount;
        public int reviewCount;
        public int negativeCount; // rating <= 2
    }

    public boolean insertFeedback(Feedback f) {
        String sql = "INSERT INTO customer_feedback (customer_name, contact_number, feedback_type, rating, comments, feedback_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, f.getCustomerName());
            pstmt.setString(2, f.getContactNumber());
            pstmt.setString(3, f.getFeedbackType());
            pstmt.setInt(4, f.getRating());
            pstmt.setString(5, f.getComments());
            pstmt.setDate(6, f.getFeedbackDate());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        f.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Feedback> getAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM customer_feedback ORDER BY feedback_date DESC, id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Feedback(
                    rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getString("contact_number"),
                    rs.getString("feedback_type"),
                    rs.getInt("rating"),
                    rs.getString("comments"),
                    rs.getDate("feedback_date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateFeedback(Feedback f) {
        String sql = "UPDATE customer_feedback SET customer_name = ?, contact_number = ?, feedback_type = ?, rating = ?, comments = ?, feedback_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, f.getCustomerName());
            pstmt.setString(2, f.getContactNumber());
            pstmt.setString(3, f.getFeedbackType());
            pstmt.setInt(4, f.getRating());
            pstmt.setString(5, f.getComments());
            pstmt.setDate(6, f.getFeedbackDate());
            pstmt.setInt(7, f.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteFeedback(int id) {
        String sql = "DELETE FROM customer_feedback WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Feedback> searchFeedback(String nameQuery, String typeFilter, Integer ratingFilter, String dateStr) {
        List<Feedback> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM customer_feedback WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nameQuery != null && !nameQuery.trim().isEmpty()) {
            sql.append(" AND customer_name LIKE ?");
            params.add("%" + nameQuery.trim() + "%");
        }
        if (typeFilter != null && !typeFilter.equals("All")) {
            sql.append(" AND feedback_type = ?");
            params.add(typeFilter);
        }
        if (ratingFilter != null && ratingFilter > 0) {
            sql.append(" AND rating = ?");
            params.add(ratingFilter);
        }
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            sql.append(" AND feedback_date = ?");
            try {
                params.add(java.sql.Date.valueOf(dateStr.trim()));
            } catch (IllegalArgumentException e) {
                // If invalid date format, we don't apply date filter or match nothing.
                sql.append(" AND 1=0"); 
            }
        }
        
        sql.append(" ORDER BY feedback_date DESC, id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Feedback(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("contact_number"),
                        rs.getString("feedback_type"),
                        rs.getInt("rating"),
                        rs.getString("comments"),
                        rs.getDate("feedback_date")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public FeedbackStats getFeedbackStats() {
        FeedbackStats stats = new FeedbackStats();
        String sql = "SELECT " +
                     "  AVG(rating) as avg_rating, " +
                     "  COUNT(*) as total, " +
                     "  SUM(CASE WHEN feedback_type = 'Complaint' THEN 1 ELSE 0 END) as complaints, " +
                     "  SUM(CASE WHEN feedback_type = 'Suggestion' THEN 1 ELSE 0 END) as suggestions, " +
                     "  SUM(CASE WHEN feedback_type = 'Review' THEN 1 ELSE 0 END) as reviews, " +
                     "  SUM(CASE WHEN rating <= 2 THEN 1 ELSE 0 END) as negative " +
                     "FROM customer_feedback";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                stats.averageRating = rs.getDouble("avg_rating");
                stats.totalCount = rs.getInt("total");
                stats.complaintCount = rs.getInt("complaints");
                stats.suggestionCount = rs.getInt("suggestions");
                stats.reviewCount = rs.getInt("reviews");
                stats.negativeCount = rs.getInt("negative");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}
