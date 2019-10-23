package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class RegisterController {
    LoginController loginController;

    public void setLoginController(LoginController loginController) { this.loginController = loginController; }

    private void goToLogin() { ScreenController.getInstance().activate(ScreenController.Screen.LOGIN); }

    @FXML
    void confirmRegistration(ActionEvent event) {
        //TODO: Send data back to login
        goToLogin();
    }

    @FXML
    void cancelRegistration(MouseEvent event) {
        goToLogin();
    }
}
