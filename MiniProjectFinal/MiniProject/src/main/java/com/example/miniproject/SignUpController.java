package com.example.miniproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {

    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private DatePicker dob;
    @FXML private TextField email;
    @FXML private TextField contactNo;
    @FXML private TextField studentID;
    @FXML private Label errorMessageLabel; // Add this to connect to the Label in FXML

    @FXML public Hyperlink logIn;

    @FXML
    private void handleRegister(ActionEvent event) {
        // Clear the error message label
        errorMessageLabel.setText("");

        // Get user input
        String usernameValue = getValueOrNull(username);
        String passwordValue = getValueOrNull(password);
        String firstNameValue = getValueOrNull(firstName);
        String lastNameValue = getValueOrNull(lastName);
        String dobValue = dob.getValue() != null ? dob.getValue().toString() : null;
        String emailValue = getValueOrNull(email);
        String contactNoValue = getValueOrNull(contactNo);
        String studentIDValue = getValueOrNull(studentID);

        // Validate that all required fields are filled
        if (usernameValue == null || passwordValue == null || firstNameValue == null ||
                lastNameValue == null || dobValue == null || emailValue == null ||
                contactNoValue == null || studentIDValue == null) {
            // Display error message if any required field is missing
            errorMessageLabel.setText("Please Fill All Fields!");
            return;
        }

        // Hash the password before inserting it into the database
        String hashedPassword = PasswordUtil.hashPassword(passwordValue);

        // Insert data into the database
        String DBURL = "jdbc:mysql://localhost:3306/sms"; // Replace with your DB details
        String DBUser = "root"; // Replace with your DB username
        String DBPassword = "NightCraw1er$"; // Replace with your DB password

        String insertQuery = "INSERT INTO users (username, password, date_of_birth, email, first_name, last_name, contact_no, student_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            // Set the values for the query
            stmt.setString(1, usernameValue);
            stmt.setString(2, hashedPassword);  // Store the hashed password
            stmt.setDate(3, Date.valueOf(dobValue));  // Convert LocalDate to SQL Date
            stmt.setString(4, emailValue);
            stmt.setString(5, firstNameValue);
            stmt.setString(6, lastNameValue);
            stmt.setString(7, contactNoValue);
            stmt.setString(8, studentIDValue);

            // Execute the query
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                // Load login page upon successful registration
                Parent loginPageParent = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
                Scene loginPageScene = new Scene(loginPageParent);

                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(loginPageScene);
                window.show();
            } else {
                errorMessageLabel.setText("Registration failed, please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Log the exception
            errorMessageLabel.setText("Database error occurred. Please try again later.");
        } catch (IOException e) {
            e.printStackTrace();  // Log the exception
            errorMessageLabel.setText("UI error occurred. Please contact support.");
        }
    }

    @FXML
    public void handleLoginPage(ActionEvent event) throws IOException {
        Parent loginPageParent = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
        Scene loginPageScene = new Scene(loginPageParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginPageScene);
        window.show();
    }

    /**
     * Utility method to get the text value from a TextField or PasswordField safely.
     * If the field is null or empty, it returns null.
     */
    private String getValueOrNull(TextField textField) {
        if (textField != null && !textField.getText().trim().isEmpty()) {
            return textField.getText().trim();
        }
        return null;
    }

    /**
     * Overloaded utility method to get the text value from a PasswordField safely.
     * If the field is null or empty, it returns null.
     */
    private String getValueOrNull(PasswordField passwordField) {
        if (passwordField != null && !passwordField.getText().trim().isEmpty()) {
            return passwordField.getText().trim();
        }
        return null;
    }
}
