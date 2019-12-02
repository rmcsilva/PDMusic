package sample.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import sample.exceptions.CountExceededException;

import java.io.IOException;

public class RegisterController {

    private LoginController loginController;

    @FXML
    private JFXTextField nameField, usernameField;

    @FXML
    private JFXPasswordField passwordField;

    private String name, username, password;

    public void setLoginController(LoginController loginController) { this.loginController = loginController; }

    private void goToLogin() { ScreenController.getInstance().activate(ScreenController.Screen.LOGIN); }

    private boolean getFieldValues() {
        name = nameField.getText();
        username = usernameField.getText();
        password = passwordField.getText();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return false;
        }

        return true;
    }

    private void clearField() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    @FXML
    void confirmRegistration(ActionEvent event) throws IOException, CountExceededException {
        if (!getFieldValues()) {
            return;
        }
        //TODO: Send data back to login
        loginController.startCommunicationHandler();
        loginController.getCommunicationHandler().register(name, username, password);
        loginController.setUsernameFieldText(username);

        clearField();
    }

    @FXML
    void cancelRegistration(MouseEvent event) {
        goToLogin();
    }
}
