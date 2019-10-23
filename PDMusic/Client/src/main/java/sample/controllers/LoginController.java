package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private RegisterController registerController = null;
    private ScreenController screenController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenController = ScreenController.getInstance();

        //Add Register Screen
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/register.fxml"));
            screenController.addScreen(ScreenController.Screen.REGISTER, fxmlLoader.load());
            registerController = fxmlLoader.getController();
            registerController.setLoginController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
