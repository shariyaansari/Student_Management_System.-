package com.example.miniproject;

import java.time.LocalDate;

public class Attendance {
    private Integer studentId;
    private String lecture;
    private double duration;
    private LocalDate date; // New field for the date

    public Attendance(Integer studentId, String lecture, double duration) {
        this.studentId = studentId;
        this.lecture = lecture;
        this.duration = duration;
        this.date = date; // Initialize the date
    }

    public Integer getStudentId() {
        return studentId;
    }

    public String getLecture() {
        return lecture;
    }

    public double getDuration() {
        return duration;
    }

    public LocalDate getDate() {
        return date; // Getter for the date
    }

    // Optionally, you can override toString() for better logging or debugging
    @Override
    public String toString() {
        return "Attendance{" +
                "studentId=" + studentId +
                ", lecture='" + lecture + '\'' +
                ", duration=" + duration +
                ", date=" + date +
                '}';
    }
}
