package com.example.miniproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminController {

    @FXML
    private TextField studentIdField;
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

    public TableColumn attendanceStudentIdColumn;
    public TableColumn attendanceLectureColumn;
    public TableColumn attendanceDurationColumn;
    public TableView attendanceTableView;
    public Button deleteAttendanceButton;
    @FXML
    private DatePicker attendanceDatePicker;
    @FXML
    private Button searchButton;

    @FXML
    public Button addAttendanceButton;
    @FXML
    private TableView<Student> studentInfoTableView;

    @FXML
    private TableColumn<Student, String> studentIdColumn;
    @FXML
    private TableColumn<Student, String> usernameColumn;
    @FXML
    private TableColumn<Student, String> firstNameColumn;
    @FXML
    private TableColumn<Student, String> lastNameColumn;
    @FXML
    private TableColumn<Student, String> phoneNoColumn;
    @FXML
    private TableColumn<Student, String> emailIdColumn;
    @FXML
    private TableColumn<Student, String> dobColumn;

    @FXML
    private TableView<Assignment> assignmentTableView; // Table to display assignments
    @FXML
    private TableColumn<Assignment, String> assignmentIdColumn;
    @FXML
    private TableColumn<Assignment, String> assignmentTitleColumn;
    @FXML
    private TableColumn<Assignment, String> submissionDateColumn;

    @FXML
    private TextField studentIdTextField; // TextField for Student ID input

    private final String DBURL = "jdbc:mysql://localhost:3306/sms";
    private final String DBUser = "root";
    private final String DBPassword = "NightCraw1er$";

    @FXML
    public void initialize() {
        // Initialize student table columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNoColumn.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        emailIdColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        // Initialize assignment table columns
        assignmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        assignmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("submissionDate"));

        attendanceStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        attendanceLectureColumn.setCellValueFactory(new PropertyValueFactory<>("lecture"));
        attendanceDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        ia1.setOnAction(e -> exampick.setText(ia1.getText()));
        ia2.setOnAction(e -> exampick.setText(ia2.getText()));
        ese.setOnAction(e -> exampick.setText(ese.getText()));

        searchButton.setOnAction(event -> searchAttendanceByDate());

        deleteAttendanceButton.setOnAction(event -> deleteSelectedAttendance(attendanceTableView));

        addAttendanceButton.setOnAction(event -> showAddAttendancePopup());
        loadStudentData();
    }

    private void loadStudentData() {
        String query = "SELECT student_id, username, first_name, last_name, contact_no, email, date_of_birth FROM users";
        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             var stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            while (resultSet.next()) {
                Student student = new Student(
                        resultSet.getString("student_id"),
                        resultSet.getString("username"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("contact_no"),
                        resultSet.getString("email"),
                        resultSet.getString("date_of_birth"),
                        null // Password is not shown in the table
                );
                studentInfoTableView.getItems().add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load student data.");
        }
    }

    @FXML
    private void handleAdd() {
        Student newStudent = showStudentDialog(new Student("", "", "", "", "", "", "", ""), "Add New Student", false);
        if (newStudent != null) {
            addStudentToDatabase(newStudent);
            studentInfoTableView.getItems().add(newStudent);
        }
    }

    @FXML
    private void handleUpdate() {
        Student selectedStudent = studentInfoTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            Student updatedStudent = showStudentDialog(selectedStudent, "Update Student Information", true);
            if (updatedStudent != null) {
                updateStudentInDatabase(updatedStudent);
                int index = studentInfoTableView.getItems().indexOf(selectedStudent);
                studentInfoTableView.getItems().set(index, updatedStudent); // Update table directly
            }
        } else {
            showAlert("No Selection", "Please select a student to update.");
        }
    }

    @FXML
    private void handleDelete() {
        Student selectedStudent = studentInfoTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            if (confirmDelete(selectedStudent)) {
                deleteStudentFromDatabase(selectedStudent.studentId);
                studentInfoTableView.getItems().remove(selectedStudent);
            }
        } else {
            showAlert("No Selection", "Please select a student to delete.");
        }
    }

    @FXML
    private void handleViewAssignments() {
        String studentId = studentIdTextField.getText().trim(); // Get the Student ID from TextField
        if (!studentId.isEmpty()) {
            loadAssignmentsForStudent(studentId);
        } else {
            showAlert("No Input", "Please enter a Student ID to view assignments.");
        }
    }

    private void loadAssignmentsForStudent(String studentId) {
        String query = "SELECT s.id, s.file_name, s.submission_date " +
                "FROM submissions s " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE u.student_id = ?";

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             var pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentId);
            ResultSet resultSet = pstmt.executeQuery();

            assignmentTableView.getItems().clear(); // Clear previous data
            while (resultSet.next()) {
                Assignment assignment = new Assignment(
                        resultSet.getString("id"),
                        resultSet.getString("file_name"),
                        resultSet.getString("submission_date")
                );
                assignmentTableView.getItems().add(assignment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load assignments.");
        }
    }


    private Student showStudentDialog(Student student, String title, boolean isUpdate) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType actionButtonType = new ButtonType(isUpdate ? "Update" : "Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(actionButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        TextField studentIdField = new TextField(student.studentId);
        TextField usernameField = new TextField(student.username);
        TextField firstNameField = new TextField(student.firstName);
        TextField lastNameField = new TextField(student.lastName);
        TextField contactNoField = new TextField(student.contactNo);
        TextField emailField = new TextField(student.email);
        TextField dobField = new TextField(student.dateOfBirth);
        PasswordField passwordField = new PasswordField();

        // Password field is only shown for adding a student
        if (!isUpdate) {
            grid.add(new Label("Password:"), 0, 7);
            grid.add(passwordField, 1, 7);
        }

        grid.add(new Label("Student ID:"), 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(new Label("Contact No:"), 0, 4);
        grid.add(contactNoField, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);
        grid.add(new Label("Date of Birth:"), 0, 6);
        grid.add(dobField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == actionButtonType) {
                return new Student(
                        studentIdField.getText(),
                        usernameField.getText(),
                        firstNameField.getText(),
                        lastNameField.getText(),
                        contactNoField.getText(),
                        emailField.getText(),
                        dobField.getText(),
                        passwordField.getText() // Password captured if adding a new student
                );
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void addStudentToDatabase(Student student) {
        String hashedPassword = PasswordUtil.hashPassword(student.getPassword()); // Hash the password

        String insertQuery = "INSERT INTO users (student_id, username, first_name, last_name, contact_no, email, date_of_birth, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             var pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getUsername());
            pstmt.setString(3, student.getFirstName());
            pstmt.setString(4, student.getLastName());
            pstmt.setString(5, student.getContactNo());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getDateOfBirth());
            pstmt.setString(8, hashedPassword); // Insert the hashed password

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not add new student.");
        }
    }

    private void updateStudentInDatabase(Student student) {
        String query = "UPDATE users SET username = ?, first_name = ?, last_name = ?, contact_no = ?, email = ?, date_of_birth = ? WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             var pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, student.username);
            pstmt.setString(2, student.firstName);
            pstmt.setString(3, student.lastName);
            pstmt.setString(4, student.contactNo);
            pstmt.setString(5, student.email);
            pstmt.setString(6, student.dateOfBirth);
            pstmt.setString(7, student.studentId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not update student information.");
        }
    }

    private void deleteStudentFromDatabase(String studentId) {
        String deleteUserTasksQuery = "DELETE FROM user_tasks WHERE user_id = (SELECT id FROM users WHERE student_id = ?)";
        String deleteUserQuery = "DELETE FROM users WHERE student_id = ?";

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             var pstmtUserTasks = conn.prepareStatement(deleteUserTasksQuery);
             var pstmtUser = conn.prepareStatement(deleteUserQuery)) {

            // Delete related tasks first
            pstmtUserTasks.setString(1, studentId);
            pstmtUserTasks.executeUpdate();

            // Now delete the user
            pstmtUser.setString(1, studentId);
            pstmtUser.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not delete student.");
        }
    }

    private boolean confirmDelete(Student student) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete student " + student.firstName + " " + student.lastName + "?");
        alert.setContentText("Are you sure you want to delete this student?");

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAddAttendancePopup() {
        // Create the popup window
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Attendance");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20)); // Add padding for better spacing

        // Text fields for student ID, lecture, duration, and date picker
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID");

        TextField lectureField = new TextField();
        lectureField.setPromptText("Lecture");

        TextField durationField = new TextField();
        durationField.setPromptText("Duration");

        DatePicker datePicker = new DatePicker();

        // Add all fields to the layout
        layout.getChildren().addAll(
                new Label("Student ID:"), studentIdField,
                new Label("Lecture:"), lectureField,
                new Label("Duration:"), durationField,
                new Label("Date:"), datePicker
        );

        Button submitButton = new Button("Submit");

        // Define the submit action
        submitButton.setOnAction(e -> handleAttendanceSubmission(studentIdField, lectureField, durationField, datePicker, popupStage));

        // Add the key event handler to the layout
        layout.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleAttendanceSubmission(studentIdField, lectureField, durationField, datePicker, popupStage);
            }
        });

        layout.getChildren().add(submitButton);

        // Adjust the scene size to be larger for better visibility
        Scene scene = new Scene(layout, 350, 350); // Increase the window size (350x350)
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void handleAttendanceSubmission(TextField studentIdField, TextField lectureField, TextField durationField, DatePicker datePicker, Stage popupStage) {
        // Get the entered details
        String studentId = studentIdField.getText();
        String lecture = lectureField.getText();
        String duration = durationField.getText(); // Duration in hours as a string
        LocalDate date = datePicker.getValue();

        try {
            // Convert the studentId to int for validation
            int studentIdInt = Integer.parseInt(studentId);

            // Call checkStudentExists to ensure the student ID is valid
            if (!checkStudentExists(studentIdInt)) {
                showAlert("Invalid Student ID", "Student ID " + studentId + " does not exist.");
                return; // Stop further execution
            }

            // Proceed with saving attendance if student exists
            double durationInHours = Double.parseDouble(duration); // Convert the string to a double

            // Call the method to insert attendance into the database
            if (saveAttendanceToDatabase(studentId, lecture, durationInHours, date)) {
                // Successfully saved - show success alert
                showAlert("Success", "Attendance saved successfully!");
            } else {
                showAlert("Error", "Error saving attendance.");
            }

            // Close the popup
            popupStage.close();
        } catch (NumberFormatException e) {
            // If the duration or student ID is not a valid number, show an error alert
            showAlert("Invalid Input", "Please enter valid numbers for student ID and duration.");
        }
    }


    private boolean saveAttendanceToDatabase(String studentId, String lecture, double duration, LocalDate date) {
        // JDBC connection URL, username, and password
        String url = "jdbc:mysql://localhost:3306/sms"; // Change to your DB URL
        String username = "root"; // Change to your DB username
        String password = "NightCraw1er$"; // Change to your DB password

        // SQL query to insert data
        String sql = "INSERT INTO attendance (student_id, lecture, duration, date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the values for the query
            pstmt.setInt(1, Integer.parseInt(studentId)); // Assuming student_id is an integer
            pstmt.setString(2, lecture);
            pstmt.setDouble(3, duration); // Save duration as double (hours)
            pstmt.setDate(4, java.sql.Date.valueOf(date)); // Convert LocalDate to java.sql.Date

            // Execute the insert
            pstmt.executeUpdate();
            return true; // Return true if the insert was successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }

    public boolean checkStudentExists(int studentId) {
        String query = "SELECT COUNT(*) FROM users WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count is greater than 0, student exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void searchAttendanceByDate() {
        LocalDate selectedDate = attendanceDatePicker.getValue();

        if (selectedDate == null) {
            showAlert("No Date Selected", "Please select a date to search for attendance.");
            return;
        }

        String query = "SELECT student_id, lecture, duration FROM attendance WHERE date = ?";
        attendanceTableView.getItems().clear(); // Clear previous data

        try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, java.sql.Date.valueOf(selectedDate)); // Convert LocalDate to java.sql.Date
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                Attendance attendance = new Attendance(
                        resultSet.getInt("student_id"),
                        resultSet.getString("lecture"),
                        resultSet.getDouble("duration")
                );
                attendanceTableView.getItems().add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load attendance data.");
        }
    }

    private void deleteSelectedAttendance(TableView<Attendance> attendanceTableView) {
        Attendance selectedAttendance = (Attendance) this.attendanceTableView.getSelectionModel().getSelectedItem();
        LocalDate selectedDate = attendanceDatePicker.getValue();

        if (selectedAttendance != null && selectedDate != null) {
            String deleteQuery = "DELETE FROM attendance WHERE student_id = ? AND date = ? AND lecture = ?";

            try (Connection conn = DriverManager.getConnection(DBURL, DBUser, DBPassword);
                 PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

                pstmt.setInt(1, selectedAttendance.getStudentId()); // Student ID
                pstmt.setDate(2, java.sql.Date.valueOf(selectedDate)); // Date from DatePicker
                pstmt.setString(3, selectedAttendance.getLecture()); // Lecture from the selected record

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Attendance record deleted successfully!");
                    alert.showAndWait();

                    // Remove the item from the TableView
                    this.attendanceTableView.getItems().remove(selectedAttendance);
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to delete the attendance record.");
                    alert.showAndWait();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while trying to delete the record.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an attendance record and a date to delete.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSubmit() {
        String studentId = studentIdField.getText();
        String exam = exampick.getText();
        String dsa = dsamarks.getText();
        String dbms = dbmsmarks.getText();
        String poc = pocmarks.getText();
        String pcpf = pcpfmarks.getText();
        String maths = mathsmarks.getText();
        String java = javamarks.getText();

        if (studentId.isEmpty() || exam.isEmpty()) {
            showAlert("Error", "Please enter Student ID and choose an exam.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DBURL, DBUser, DBPassword)) {
            // Check if student_id exists in users table
            String checkUserQuery = "SELECT * FROM users WHERE student_id = ?";
            PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery);
            checkUserStatement.setString(1, studentId);
            ResultSet resultSet = checkUserStatement.executeQuery();

            if (resultSet.next()) {
                // Student ID exists, proceed with inserting marks
                String insertMarksQuery = "INSERT INTO studentmarks (student_id, exam, dsa, dbms, poc, pcpf, math, java) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertMarksQuery);

                insertStatement.setString(1, studentId);
                insertStatement.setString(2, exam);
                insertStatement.setString(3, dsa);
                insertStatement.setString(4, dbms);
                insertStatement.setString(5, poc);
                insertStatement.setString(6, pcpf);
                insertStatement.setString(7, maths);
                insertStatement.setString(8, java);

                int rowsInserted = insertStatement.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert("Success", "Marks submitted successfully.");
                }
            } else {
                // Student ID doesn't exist
                showAlert("Error", "Student ID not found in the database.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to submit marks: " + e.getMessage());
        }
    }

    public void handleLoginPage(ActionEvent e) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
