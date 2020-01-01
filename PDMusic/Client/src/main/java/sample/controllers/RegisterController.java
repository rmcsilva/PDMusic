package sample.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import sample.controllers.communication.Exceptions.NoServerAvailable;
import sample.exceptions.NoServersDirectory;
import sample.exceptions.CountExceededException;

import java.io.IOException;

public class RegisterController {

    private LoginController loginController;

    @FXML
    private JFXTextField nameField, usernameField;

    @FXML
    private JFXPasswordField passwordField;

    private String name, username, password;

    private ScreenController screenController = ScreenController.getInstance();

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
        try {
            loginController.startCommunicationHandler();
        } catch (NoServerAvailable nsa) {
            screenController.showDialog("No Available Servers", nsa.getMessage() + " Try again later!");
            return;
        } catch (NoServersDirectory nsd) {
            screenController.showDialog("No Servers Directory", nsd.getMessage() + " Try again later!");
            return;
        }

        loginController.getCommunicationHandler().register(name, username, password);
        loginController.setUsernameFieldText(username);

        clearField();
    }

    @FXML
    void cancelRegistration(MouseEvent event) {
        goToLogin();
    }
}
