package com.example.miniproject;

public class UserSession {

    private static int loggedInUserId;

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void setLoggedInUserId(int studentId) {
        loggedInUserId = studentId;
    }
}