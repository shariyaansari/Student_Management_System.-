package com.example.miniproject;

public class Student {
    public String studentId;
    public String username;
    public String firstName;
    public String lastName;
    public String contactNo;
    public String email;
    public String dateOfBirth;
    public String password;

    // Constructor
    public Student(String studentId, String username, String firstName, String lastName,
                   String contactNo, String email, String dateOfBirth, String password) {
        this.studentId = studentId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNo = contactNo;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
    }

    // Getters for each field
    public String getStudentId() { return studentId; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getContactNo() { return contactNo; }
    public String getEmail() { return email; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getPassword() { return password; }

    // Setters for each field
    public void setUsername(String username) { this.username = username; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
    public void setEmail(String email) { this.email = email; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setPassword(String password) { this.password = password; }
}
