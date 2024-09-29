package com.example.miniproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PasswordController {

    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private final String DBURL = "jdbc:mysql://localhost:3306/sms"; // Replace with your DB details
    private final String DBUser = "root"; // Replace with your DB username
    private final String DBPassword = "NightCraw1er$"; // Replace with your DB password

    @FXML
    public void handlePasswordReset(ActionEvent event) {

        // Get user input from the form fields
        String email = emailField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input fields
        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        // Check if new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New Password and Confirm Password do not match.");
            return;
        }

        // Update password in the database
        String updateQuery = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Set the parameters for the query
            stmt.setString(1, newPassword); // Remember to hash the password in a real application
            stmt.setString(2, email);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert("Success", "Password has been reset successfully!");

                // Redirect to login page
                Parent loginPageParent = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
                Scene loginPageScene = new Scene(loginPageParent);

                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(loginPageScene);
                window.show();
            } else {
                showAlert("Error", "Email not found. Please enter the registered email.");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "Database error. Please try again later.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

