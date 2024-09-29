package com.example.miniproject;

public class Assignment {
    private String id;               // Assignment ID
    private String fileName;         // File name of the assignment
    private String submissionDate;    // Submission date of the assignment

    // Constructor
    public Assignment(String id, String fileName, String submissionDate) {
        this.id = id;
        this.fileName = fileName;
        this.submissionDate = submissionDate;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }
}
