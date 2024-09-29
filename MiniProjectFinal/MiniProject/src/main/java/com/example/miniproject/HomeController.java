package com.example.miniproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class HomeController {

    @FXML
    public ListView<String> listOfNames;
    @FXML
    public TextField name;
    @FXML
    public Button addtask;
    @FXML
    public Button removetask;

    @FXML
    public void initialize() {
        loadUserTasks();
    }

    @FXML
    public void addtask(javafx.scene.input.MouseEvent mouseEvent) {
        String task = name.getText();
        if (!task.isEmpty()) {
            // Check if the user exists
            if (isUserValid(UserSession.getLoggedInUserId())) {
                // Add the task to the ListView
                listOfNames.getItems().add(task);
                saveTaskToDatabase(task); // Save to the database
                name.clear(); // Clear the text field after adding the task
            } else {
                System.out.println("User ID is not valid. Task cannot be added.");
            }
        } else {
            System.out.println("Please enter a task.");
        }
    }

    private boolean isUserValid(int userId) {
        String DBURL = "jdbc:mysql://localhost:3306/sms"; // Replace with your DB details
        String DBUser = "root"; // Replace with your DB username
        String DBPassword = "NightCraw1er$"; // Replace with your DB password

        String selectQuery = "SELECT COUNT(*) FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Returns true if the user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error checking user validity.");
        }
        return false;
    }

    private void saveTaskToDatabase(String task) {
        String DBURL = "jdbc:mysql://localhost:3306/sms"; // Replace with your DB details
        String DBUser = "root"; // Replace with your DB username
        String DBPassword = "NightCraw1er$"; // Replace with your DB password

        String insertQuery = "INSERT INTO user_tasks (user_id, task) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, UserSession.getLoggedInUserId()); // Use the logged-in user's ID
            stmt.setString(2, task);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database error occurred while saving task.");
        }
    }

    private void loadUserTasks() {
        String DBURL = "jdbc:mysql://localhost:3306/sms"; // Replace with your DB details
        String DBUser = "root"; // Replace with your DB username
        String DBPassword = "NightCraw1er$"; // Replace with your DB password

        String selectQuery = "SELECT task FROM user_tasks WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            stmt.setInt(1, UserSession.getLoggedInUserId()); // Use the logged-in user's ID
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String task = resultSet.getString("task");
                listOfNames.getItems().add(task); // Add to the ListView
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database error occurred while loading tasks.");
        }
    }

    @FXML
    public void removetask(javafx.scene.input.MouseEvent mouseEvent) {
        int selectedIndex = listOfNames.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String removedTask = listOfNames.getItems().remove(selectedIndex);
            deleteTaskFromDatabase(removedTask); // Remove from the database as well
        } else {
            System.out.println("No task selected to remove.");
        }
    }

    private void deleteTaskFromDatabase(String task) {
        String DBURL = "jdbc:mysql://localhost:3306/sms"; // Replace with your DB details
        String DBUser = "root"; // Replace with your DB username
        String DBPassword = "NightCraw1er$"; // Replace with your DB password

        String deleteQuery = "DELETE FROM user_tasks WHERE user_id = ? AND task = ?";

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, UserSession.getLoggedInUserId()); // Use the logged-in user's ID
            stmt.setString(2, task);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database error occurred while deleting task.");
        }
    }

    public void handleHomePage(ActionEvent e) throws IOException {
        loadScene(e, "Home.fxml");
    }

    public void handleAttenPage(ActionEvent e) throws IOException {
        loadScene(e, "Attendence.fxml");
    }

    public void handleNewsPage(ActionEvent e) throws IOException {
        loadScene(e, "news.fxml");
    }

    public void handleSettingPage(ActionEvent e) throws IOException {
        loadScene(e, "settings.fxml");
    }

    public void handleAboutPage(ActionEvent e) throws IOException {
        loadScene(e, "about.fxml");
    }

    public void handleClubPage(ActionEvent e) throws IOException {
        loadScene(e, "clubs.fxml");
    }

    public void handleLoginPage(ActionEvent e) throws IOException {
        loadScene(e, "loginpage.fxml");
    }

    // Helper method to load scenes
    private void loadScene(ActionEvent e, String fxmlFile) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(fxmlFile));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
