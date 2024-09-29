//package com.example.miniproject;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.sql.*;

//public class LoginController {
//
//    @FXML public Label errorMessageLabel;
//    @FXML public Button loginButton;
//    @FXML public Hyperlink RegLink;
//    @FXML public Hyperlink PassLink;
//    @FXML public TextField usernameField;
//    @FXML public PasswordField passwordField;
//
//    private final String DBURL = "jdbc:mysql://localhost:3306/sms"; // Replace with your DB details
//    private final String DBUser = "root"; // Replace with your DB username
//    private final String DBPassword = "NightCraw1er$"; // Replace with your DB password
//
//    @FXML
//    public void handleLoginButton(ActionEvent e) {
//
//        // Get the input from the text fields
//        String username = usernameField.getText();
//        String password = passwordField.getText();
//
//        // Check if fields are not empty
//        if (username.isEmpty() || password.isEmpty()) {
//            errorMessageLabel.setText("Please enter both username and password.");
//            return;
//        }
//
//        // Check the username and password against the database
//        String query = "SELECT * FROM users WHERE username = ? AND password = ?"; // Ensure password is hashed and checked properly
//
//        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            // Set the values for the query
//            stmt.setString(1, username);
//            stmt.setString(2, password); // You should hash the password before comparing
//
//            ResultSet resultSet = stmt.executeQuery();
//
//            // If a match is found, load the next page
//            if (resultSet.next()) {
//                // If valid, load the new scene (Home.fxml)
//                Parent homePageParent = FXMLLoader.load(getClass().getResource("Home.fxml"));
//                Scene homePageScene = new Scene(homePageParent);
//
//                // Get the current stage
//                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
//
//                // Set the new scene and show the window
//                window.setScene(homePageScene);
//                window.show();
//
//            } else {
//                // If invalid, display an error message
//                errorMessageLabel.setText("Invalid username or password.");
//            }
//
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            errorMessageLabel.setText("Database error. Please try again later.");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            errorMessageLabel.setText("Error loading the next page. Please try again later.");
//        }
//    }
//
//    @FXML
//    public void handleSignUpPage(ActionEvent event) throws IOException {
//
//        Parent signUpPageParent = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
//        Scene signUpPageScene = new Scene(signUpPageParent);
//
//        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        window.setScene(signUpPageScene);
//        window.show();
//    }
//
//    @FXML
//    public void handlePasswordLink(ActionEvent e) throws IOException {
//
//        Parent PassParent = FXMLLoader.load(getClass().getResource("Forgotpass.fxml"));
//        Scene PassScene = new Scene(PassParent);
//
//        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
//        window.setScene(PassScene);
//        window.show();
//    }
//}
package com.example.miniproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML public Label errorMessageLabel;
    @FXML public Button loginButton;
    @FXML public Hyperlink RegLink;
    @FXML public Hyperlink PassLink;
    @FXML public TextField usernameField;
    @FXML public PasswordField passwordField;

    private final String DBURL = "jdbc:mysql://localhost:3306/sms"; // Replace with your DB details
    private final String DBUser = "root"; // Replace with your DB username
    private final String DBPassword = "NightCraw1er$"; // Replace with your DB password

    @FXML
    public void handleLoginButton(ActionEvent e) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Please enter both username and password.");
            return;
        }

        // Hardcoded admin credentials
        String adminUsername = "admin";
        String adminPassword = "admin123";  // Use hashed password for security

        if (username.equals(adminUsername) && PasswordUtil.checkPassword(password, PasswordUtil.hashPassword(adminPassword))) {
            // Admin login
            System.out.println("Admin logged in");

            Parent adminHomeParent = FXMLLoader.load(getClass().getResource("Admin.fxml"));
            Scene adminHomeScene = new Scene(adminHomeParent);

            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.setScene(adminHomeScene);
            window.show();
        } else {
            // Regular user login logic
            String query = "SELECT * FROM users WHERE username = ?";

            try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    String storedHashedPassword = resultSet.getString("password");
                    if (PasswordUtil.checkPassword(password, storedHashedPassword)) {
                        int studentId = resultSet.getInt("student_id");
                        UserSession.setLoggedInUserId(studentId);

                        Parent homePageParent = FXMLLoader.load(getClass().getResource("Home.fxml"));
                        Scene homePageScene = new Scene(homePageParent);

                        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        window.setScene(homePageScene);
                        window.show();
                    } else {
                        errorMessageLabel.setText("Invalid username or password.");
                    }
                } else {
                    errorMessageLabel.setText("Invalid username or password.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                errorMessageLabel.setText("Database error. Please try again later.");
            } catch (IOException ex) {
                ex.printStackTrace();
                errorMessageLabel.setText("Error loading the next page. Please try again later.");
            }
        }
    }

    @FXML
    public void handleSignUpPage(ActionEvent event) throws IOException {
        Parent signUpPageParent = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
        Scene signUpPageScene = new Scene(signUpPageParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(signUpPageScene);
        window.show();
    }

    @FXML
    public void handlePasswordLink(ActionEvent e) throws IOException {
        Parent PassParent = FXMLLoader.load(getClass().getResource("Forgotpass.fxml"));
        Scene PassScene = new Scene(PassParent);

        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        window.setScene(PassScene);
        window.show();
    }
}
