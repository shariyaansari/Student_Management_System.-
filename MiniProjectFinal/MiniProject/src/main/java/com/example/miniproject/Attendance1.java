package com.example.miniproject;
// Make sure the package name matches your project structure

public class Attendance1 {
    private String lecture; // The name of the lecture
    private String date;    // The date of the attendance
    private String duration; // The duration of the lecture

    // Constructor
    public Attendance1(String lecture, String date, double duration) {
        this.lecture = lecture;
        this.date = date;
        this.duration = String.valueOf(duration);
    }

    // Getter for lecture
    public String getLecture() {
        return lecture;
    }

    // Setter for lecture
    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    // Getter for date
    public String getDate() {
        return date;
    }

    // Setter for date
    public void setDate(String date) {
        this.date = date;
    }

    // Getter for duration
    public String getDuration() {
        return duration;
    }

    // Setter for duration
    public void setDuration(String duration) {
        this.duration = duration;
    }
}
