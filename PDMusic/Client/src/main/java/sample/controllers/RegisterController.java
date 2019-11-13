package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import sample.exceptions.CountExceededException;

import java.io.IOException;

public class RegisterController {

    LoginController loginController;

    public void setLoginController(LoginController loginController) { this.loginController = loginController; }

    private void goToLogin() { ScreenController.getInstance().activate(ScreenController.Screen.LOGIN); }

    @FXML
    void confirmRegistration(ActionEvent event) throws IOException, CountExceededException {
        //TODO: Send data back to login
        loginController.startCommunicationHandler();

        goToLogin();
    }

    @FXML
    void cancelRegistration(MouseEvent event) {
        goToLogin();
    }
}
