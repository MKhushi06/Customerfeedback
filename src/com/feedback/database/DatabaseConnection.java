package com.feedback.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {
    private static String url;
    private static String username;
    private static String password;

    static {
        try (InputStream input = DatabaseConnection.class.getResourceAsStream("/resources/db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                // Fallback to defaults if properties file not found
                url = "jdbc:mysql://localhost:3306/feedback_db?useSSL=false&allowPublicKeyRetrieval=true";
                username = "root";
                password = "Khushi@1234";
                System.out.println("Warning: db.properties not found. Using defaults.");
            } else {
                prop.load(input);
                url = prop.getProperty("db.url");
                username = prop.getProperty("db.username");
                password = prop.getProperty("db.password");
            }
            // Load driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, username, password);
    }
}
