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
    private MainController mainController = null;
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

    @FXML
    void login(ActionEvent event) {
        //TODO: Send login request

        if (mainController == null) {
            //Add Main Menu Layout
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/mainMenu.fxml"));
                screenController.addScreen(ScreenController.Screen.MAIN, fxmlLoader.load());
                mainController = fxmlLoader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //TODO: Add username to mainController
        screenController.activate(ScreenController.Screen.MAIN);
    }

    @FXML
    void register(MouseEvent event) {
        //TODO: Add the current username to the register screen
        screenController.activate(ScreenController.Screen.REGISTER);
    }
}
