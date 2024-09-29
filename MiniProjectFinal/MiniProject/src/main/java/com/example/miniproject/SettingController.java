//package com.example.miniproject;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.DatePicker;
//import javafx.scene.control.TextField;
//import javafx.scene.control.TextInputDialog;
//import javafx.scene.input.MouseEvent;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Optional;
//
//public class SettingController {
//
//    @FXML
//    private TextField userNameField;
//    @FXML
//    private TextField stdIdField; // Added field for Student ID
//    @FXML
//    private TextField firstNameField;
//    @FXML
//    private TextField lastNameField;
//    @FXML
//    private TextField phoneField;
//    @FXML
//    private TextField emailField;
//    @FXML
//    private DatePicker dobPicker;
//
//    private final String DBURL = "jdbc:mysql://localhost:3306/sms";
//    private final String DBUser = "root";
//    private final String DBPassword = "NightCraw1er$";
//
//    public void handlePasswordLink(MouseEvent e) throws IOException {
//        showAlert();
//        Parent passParent = FXMLLoader.load(getClass().getResource("Forgotpass.fxml"));
//        Scene passScene = new Scene(passParent);
//        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
//        window.setScene(passScene);
//        window.show();
//    }
//
//    public void handleHomePage(ActionEvent e) throws IOException {
//        Parent homeParent = FXMLLoader.load(getClass().getResource("Home.fxml"));
//        Scene homeScene = new Scene(homeParent);
//        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
//        window.setScene(homeScene);
//        window.show();
//    }
//
//    @FXML
//    private void saveChanges(ActionEvent event) {
//        // Get the values from the text fields and date picker
//        String userName = userNameField.getText();
//        String stdId = stdIdField.getText(); // Get the new student ID
//        String firstName = firstNameField.getText();
//        String lastName = lastNameField.getText();
//        String phone = phoneField.getText();
//        String email = emailField.getText();
//        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";
//
//        // Ask for the password to validate the user
//        TextInputDialog passwordDialog = new TextInputDialog();
//        passwordDialog.setTitle("Password Confirmation");
//        passwordDialog.setHeaderText("Enter your password to save changes");
//        passwordDialog.setContentText("Password:");
//
//        Optional<String> passwordInput = passwordDialog.showAndWait();
//
//        if (passwordInput.isPresent()) {
//            String enteredPassword = passwordInput.get();
//
//            // Update the database
//            String updateQuery = "UPDATE users SET username = ?, student_Id = ?, first_name = ?, last_name = ?, contact_no = ?, email = ?, date_of_birth = ? WHERE password = ?";
//
//            try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword);
//                 PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
//
//                preparedStatement.setString(1, userName);
//                preparedStatement.setString(2, stdId); // Update the student ID
//                preparedStatement.setString(3, firstName);
//                preparedStatement.setString(4, lastName);
//                preparedStatement.setString(5, phone);
//                preparedStatement.setString(6, email);
//                preparedStatement.setString(7, dob);
//                preparedStatement.setString(8, enteredPassword); // Use entered password for the update
//
//                int rowsAffected = preparedStatement.executeUpdate();
//
//                if (rowsAffected > 0) {
//                    showAlert("Success", "Profile updated successfully!");
//                } else {
//                    showAlert("Error", "No profile found to update with the given password.");
//                }
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//                showAlert("Database Error", "Could not update the profile: " + e.getMessage());
//            }
//        }
//    }
//
//    @FXML
//    private void deleteAccount(MouseEvent mouseEvent) {
//        // Step 1: Show a confirmation alert asking for deletion
//        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmationAlert.setTitle("Confirm Account Deletion");
//        confirmationAlert.setHeaderText("Are you sure you want to delete your account?");
//        confirmationAlert.setContentText("This action cannot be undone. Please enter your password to confirm.");
//
//        Optional<ButtonType> result = confirmationAlert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            // Step 2: If user confirms, ask for password
//            TextInputDialog passwordDialog = new TextInputDialog();
//            passwordDialog.setTitle("Password Confirmation");
//            passwordDialog.setHeaderText("Enter your password to delete the account");
//            passwordDialog.setContentText("Password:");
//
//            Optional<String> passwordInput = passwordDialog.showAndWait();
//
//            // Step 3: Check if password is correct
//            if (passwordInput.isPresent()) {
//                String enteredPassword = passwordInput.get();
//
//                if (validatePassword(enteredPassword)) {
//                    // Step 4: Delete the account if password is correct
//                    String deleteQuery = "DELETE FROM users WHERE password = ?";
//
//                    try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword);
//                         PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
//
//                        preparedStatement.setString(1, enteredPassword);
//                        int rowsAffected = preparedStatement.executeUpdate();
//
//                        if (rowsAffected > 0) {
//                            showAlert("Success", "Account deleted successfully!");
//                            navigateToLogin(mouseEvent); // Navigate to login page after deletion
//                        } else {
//                            showAlert("Error", "No account found to delete.");
//                        }
//
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                        showAlert("Database Error", "Could not delete the account: " + e.getMessage());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    // Show error if password is incorrect
//                    showAlert("Error", "Incorrect password. Account not deleted.");
//                }
//            }
//        }
//    }
//
//    private boolean validatePassword(String enteredPassword) {
//        String query = "SELECT password FROM users WHERE password = ?";
//        try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword);
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setString(1, enteredPassword);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            return resultSet.next(); // Return true if password exists
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert("Database Error", "Could not validate the password: " + e.getMessage());
//        }
//        return false;
//    }
//
//    private void navigateToLogin(MouseEvent event) throws IOException {
//        Parent loginParent = FXMLLoader.load(getClass().getResource("loginpage.fxml")); // Adjust path as needed
//        Scene loginScene = new Scene(loginParent);
//        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        window.setScene(loginScene);
//        window.show();
//    }
//
//    private void showAlert(String title, String content) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
//
//    private void showAlert() {
//        showAlert("Warning", "You will need to login again!");
//    }
//}
package com.example.miniproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SettingController {

    @FXML
    private TextField userNameField;
    @FXML
    private TextField stdIdField; // Added field for Student ID
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private DatePicker dobPicker;

    private final String DBURL = "jdbc:mysql://localhost:3306/sms";
    private final String DBUser = "root";
    private final String DBPassword = "NightCraw1er$";

    // State management: Store current user info
    private String currentUserId; // Current user's ID

    // Method to set current user ID (to be called when user logs in)
    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
        loadUserProfile(userId); // Load user profile details when user logs in
    }

    // Load user profile details from the database
    private void loadUserProfile(String userId) {
        String query = "SELECT * FROM users WHERE student_Id = ?";
        try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                userNameField.setText(resultSet.getString("username"));
                stdIdField.setText(resultSet.getString("student_Id"));
                firstNameField.setText(resultSet.getString("first_name"));
                lastNameField.setText(resultSet.getString("last_name"));
                phoneField.setText(resultSet.getString("contact_no"));
                emailField.setText(resultSet.getString("email"));
                dobPicker.setValue(resultSet.getDate("date_of_birth").toLocalDate());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not load user profile: " + e.getMessage());
        }
    }

    public void handlePasswordLink(MouseEvent e) throws IOException {
        showAlert();
        Parent passParent = FXMLLoader.load(getClass().getResource("Forgotpass.fxml"));
        Scene passScene = new Scene(passParent);
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        window.setScene(passScene);
        window.show();
    }

    public void handleHomePage(ActionEvent e) throws IOException {
        Parent homeParent = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene homeScene = new Scene(homeParent);
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        window.setScene(homeScene);
        window.show();
    }

    @FXML
    private void saveChanges(ActionEvent event) {
        // Get the values from the text fields and date picker
        String userName = userNameField.getText();
        String stdId = stdIdField.getText(); // Get the new student ID
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";

        // Ask for the password to validate the user
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Password Confirmation");
        passwordDialog.setHeaderText("Enter your password to save changes");
        passwordDialog.setContentText("Password:");

        Optional<String> passwordInput = passwordDialog.showAndWait();

        if (passwordInput.isPresent()) {
            String enteredPassword = passwordInput.get();

            // Update the database
            String updateQuery = "UPDATE users SET username = ?, student_Id = ?, first_name = ?, last_name = ?, contact_no = ?, email = ?, date_of_birth = ? WHERE student_Id = ? AND password = ?";

            try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword);
                 PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, stdId); // Update the student ID
                preparedStatement.setString(3, firstName);
                preparedStatement.setString(4, lastName);
                preparedStatement.setString(5, phone);
                preparedStatement.setString(6, email);
                preparedStatement.setString(7, dob);
                preparedStatement.setString(8, currentUserId); // Use current user's ID
                preparedStatement.setString(9, enteredPassword); // Use entered password for the update

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert("Success", "Profile updated successfully!");
                } else {
                    showAlert("Error", "No profile found to update with the given password.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Could not update the profile: " + e.getMessage());
            }
        }
    }

    @FXML
    private void deleteAccount(MouseEvent mouseEvent) {
        // Step 1: Show a confirmation alert asking for deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Account Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete your account?");
        confirmationAlert.setContentText("This action cannot be undone. Please enter your password to confirm.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Step 2: If user confirms, ask for password
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Password Confirmation");
            passwordDialog.setHeaderText("Enter your password to delete the account");
            passwordDialog.setContentText("Password:");

            Optional<String> passwordInput = passwordDialog.showAndWait();

            // Step 3: Check if password is correct
            if (passwordInput.isPresent()) {
                String enteredPassword = passwordInput.get();

                if (validatePassword(enteredPassword)) {
                    // Step 4: Delete the account if password is correct
                    String deleteQuery = "DELETE FROM users WHERE student_Id = ? AND password = ?";

                    try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword);
                         PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

                        preparedStatement.setString(1, currentUserId); // Use current user's ID
                        preparedStatement.setString(2, enteredPassword);
                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            showAlert("Success", "Account deleted successfully!");
                            navigateToLogin(mouseEvent); // Navigate to login page after deletion
                        } else {
                            showAlert("Error", "No account found to delete.");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Database Error", "Could not delete the account: " + e.getMessage());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Show error if password is incorrect
                    showAlert("Error", "Incorrect password. Account not deleted.");
                }
            }
        }
    }

    private boolean validatePassword(String enteredPassword) {
        String query = "SELECT password FROM users WHERE student_Id = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, currentUserId); // Use current user's ID
            preparedStatement.setString(2, enteredPassword);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next(); // Return true if password exists

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not validate the password: " + e.getMessage());
        }
        return false;
    }

    private void navigateToLogin(MouseEvent event) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("loginpage.fxml")); // Adjust path as needed
        Scene loginScene = new Scene(loginParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert() {
        showAlert("Warning", "You will need to login again!");
    }
}
