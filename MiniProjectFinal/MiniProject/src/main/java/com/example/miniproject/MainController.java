package com.example.miniproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    public void handleLoginPage(ActionEvent event) throws IOException {

        Parent loginPageParent = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
        Scene loginPageScene = new Scene(loginPageParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginPageScene);
        window.show();
    }

    public void handleSignUpPage(ActionEvent event) throws IOException {

        Parent signUpPageParent = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
        Scene signUpPageScene = new Scene(signUpPageParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(signUpPageScene);
        window.show();
    }
}