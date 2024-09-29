package com.example.miniproject;

import java.sql.*;
import java.time.LocalDate; // Add this import for date handling
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class AttendanceController {

    @FXML
    private TextField dsamarks;
    @FXML
    private TextField dbmsmarks;
    @FXML
    private TextField pocmarks;
    @FXML
    private TextField pcpfmarks;
    @FXML
    private TextField mathsmarks;
    @FXML
    private TextField javamarks;
    @FXML
    private MenuButton exampick;
    @FXML
    private MenuItem ia1;
    @FXML
    private MenuItem ia2;
    @FXML
    private MenuItem ese;

    public TableView attendanceTableView;
    public TableColumn lectureColumn;
    public TableColumn dateColumn;
    public TableColumn durationColumn;
    @FXML
    private Label filePathLabel;
    @FXML
    private ListView<String> fileListView;

    private List<File> selectedFiles;

    // Define where the files will be saved
    private static final String UPLOAD_DIR = "assignments/";

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sms"; // Adjust this to your DB URL
    private static final String DB_USER = "root"; // Replace with your DB username
    private static final String DB_PASSWORD = "NightCraw1er$"; // Replace with your DB password

    @FXML
    public void initialize() {

        ia1.setOnAction(e -> loadMarks("IA-1"));
        ia2.setOnAction(e -> loadMarks("IA-2"));
        ese.setOnAction(e -> loadMarks("END SEM"));

        lectureColumn.setCellValueFactory(new PropertyValueFactory<>("lecture"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        loadUserAttendance();

        loadUserSubmissions(); // Load previous submissions on initialization
    }

    @FXML
    public void handleChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Assignment Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Doc Files", "*.docx"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) filePathLabel.getScene().getWindow();
        selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        updateFilePathLabel();
    }

    private void updateFilePathLabel() {
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            StringBuilder fileNames = new StringBuilder();

            // Collect names of selected files for the label
            for (File file : selectedFiles) {
                fileNames.append(file.getName()).append("\n");
            }

            // Display the names of selected files on the label
            filePathLabel.setText(fileNames.toString().trim());
        } else {
            resetFileSelection();
        }
    }

    private void resetFileSelection() {
        filePathLabel.setText("No File Selected");
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        // Check if selectedFiles is null or empty
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            resetFileSelection();
        } else {
            System.out.println("Selected Files: " + selectedFiles); // Debugging
            if (showConfirmationDialog()) {
                submitSelectedFiles();
            } else {
                System.out.println("Submission canceled.");
            }
        }
    }

    private boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Submission");
        alert.setHeaderText("Are you sure you want to submit the selected assignments?");
        alert.setContentText("You will not be able to edit your assignments after submission.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void submitSelectedFiles() {
        for (File file : selectedFiles) {
            saveFile(file);

            // Get the original file name (without user ID) and add to ListView
            String originalFileName = file.getName();
            fileListView.getItems().add(originalFileName);
        }

        // Clear selected files from the label and reset
        resetFileSelection();
        selectedFiles = null;
    }


    // Method to save the file to the upload directory and to the database
    private void saveFile(File file) {
        try {
            // Ensure the upload directory exists, create if necessary
            Path uploadDirPath = Path.of(UPLOAD_DIR);
            if (Files.notExists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath); // Creates directory if it doesn't exist
            }

            // Append the user ID to the filename to maintain user-specific submissions
            String newFileName = file.getName();
            Path destination = uploadDirPath.resolve(newFileName);

            // Copy the file to the destination (replace if it already exists)
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File saved to: " + destination);

            // Save to database
            saveFileToDatabase(newFileName, destination.toString());
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error alert if something goes wrong
            showAlert("File Upload Error",
                    "There was an error saving the file: " + file.getName());
        }
    }

    private void saveFileToDatabase(String fileName, String filePath) {
        String insertQuery = "INSERT INTO submissions (user_id, file_name, file_path) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, UserSession.getLoggedInUserId()); // Use the logged-in user's ID
            stmt.setString(2, fileName);
            stmt.setString(3, filePath);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error",
                    "There was an error saving the submission to the database.");
        }
    }

    private void loadUserSubmissions() {
        String selectQuery = "SELECT file_name FROM submissions WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            stmt.setInt(1, UserSession.getLoggedInUserId()); // Use the logged-in user's ID
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String fileName = resultSet.getString("file_name");

                // Split to remove the user ID prefix (userId_fileName)
                String originalFileName = fileName.substring(fileName.indexOf('_') + 1);

                // Add only the original file name to the ListView
                fileListView.getItems().add(originalFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error",
                    "There was an error loading submissions from the database.");
        }
    }

    private void loadUserAttendance() {
        String selectQuery = "SELECT lecture, date, duration FROM attendance WHERE student_id = ?"; // Use student_id directly

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            // Use the logged-in user's ID from UserSession
            stmt.setInt(1, UserSession.getLoggedInUserId());
            ResultSet resultSet = stmt.executeQuery();

            List<Attendance1> attendanceList = new ArrayList<>();

            while (resultSet.next()) {
                String lecture = resultSet.getString("lecture");
                String date = resultSet.getString("date");
                double duration = resultSet.getDouble("duration");

                // Assuming you're using a model class for attendance records
                Attendance1 attendance = new Attendance1(lecture, date, duration);
                attendanceList.add(attendance); // Add to the list
            }

            // Sort the attendance list by date in descending order
            attendanceList.sort((a1, a2) -> LocalDate.parse(a2.getDate()).compareTo(LocalDate.parse(a1.getDate())));

            // Add sorted attendance records to the TableView
            attendanceTableView.getItems().addAll(attendanceList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error",
                    "There was an error loading attendance from the database.");
        }
    }

    private void loadMarks(String examType) {
        exampick.setText(examType);

        String studentId = String.valueOf(UserSession.getLoggedInUserId()); // Retrieve the student ID from session

        if (studentId == null) {
            showAlert("Error", "No student is logged in.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT dsa, dbms, poc, pcpf, math, java FROM studentmarks WHERE student_id = ? AND exam = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studentId);
            statement.setString(2, examType);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                dsamarks.setText(resultSet.getString("dsa"));
                dbmsmarks.setText(resultSet.getString("dbms"));
                pocmarks.setText(resultSet.getString("poc"));
                pcpfmarks.setText(resultSet.getString("pcpf"));
                mathsmarks.setText(resultSet.getString("math"));
                javamarks.setText(resultSet.getString("java"));
            } else {
                showAlert("No Data", "No marks found for the selected exam.");
                clearMarksFields();
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load marks: " + e.getMessage());
        }
    }

    // Method to clear marks fields if no data is found
    private void clearMarksFields() {
        dsamarks.clear();
        dbmsmarks.clear();
        pocmarks.clear();
        pcpfmarks.clear();
        mathsmarks.clear();
        javamarks.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
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
