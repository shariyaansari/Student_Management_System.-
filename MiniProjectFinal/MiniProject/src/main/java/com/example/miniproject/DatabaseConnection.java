package com.example.miniproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static void main(String[] args) {
        // Database URL, Username, and Password
        String DBURL = "jdbc:mysql://localhost:3306/sms";
        String DBUser = "root";
        String DBPassword = "NightCraw1er$";

        // Establishing a Connection
        try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword)) {
            // Check if connection is successful
            if (connection != null) {
                System.out.println("Connected to the database successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
}
