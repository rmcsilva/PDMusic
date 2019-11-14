package sample.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import sample.controllers.communication.CommunicationHandler;
import sample.exceptions.CountExceededException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.controllers.LayoutsConstants.LAYOUT_MAIN_MENU;
import static sample.controllers.LayoutsConstants.LAYOUT_REGISTER;

public class LoginController implements Initializable {

    private RegisterController registerController = null;
    private MainController mainController = null;
    private ScreenController screenController;

    private String serversDirectoryIP;
    private CommunicationHandler communicationHandler = null;

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    public void setUsernameFieldText(String username) {
        usernameField.setText(username);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenController = ScreenController.getInstance();

        //Add Register Screen
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_REGISTER));
            screenController.addScreen(ScreenController.Screen.REGISTER, fxmlLoader.load());
            registerController = fxmlLoader.getController();
            registerController.setLoginController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected CommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    public void setServersDirectoryIP(String serversDirectoryIP) {
        this.serversDirectoryIP = serversDirectoryIP;
    }

    public void startCommunicationHandler() throws IOException, CountExceededException {
        //TODO: Catch exceptions and show alerts based on them
        if (communicationHandler == null || !communicationHandler.isRunning()) {
            communicationHandler = new CommunicationHandler(serversDirectoryIP);
            communicationHandler.start();
        }
    }

    @FXML
    void login(ActionEvent event) throws IOException, CountExceededException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        startCommunicationHandler();

        if (mainController == null) {
            //Add Main Menu Layout
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_MAIN_MENU));
                screenController.addScreen(ScreenController.Screen.MAIN, fxmlLoader.load());
                mainController = fxmlLoader.getController();
                mainController.setCommunicationHandler(communicationHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mainController.setCommunicationHandler(communicationHandler);

        passwordField.clear();

        communicationHandler.login(username, password);
        screenController.activate(ScreenController.Screen.MAIN);

    }

    @FXML
    void register(MouseEvent event) throws IOException, CountExceededException {
        //TODO: Add the current username to the register screen
        screenController.activate(ScreenController.Screen.REGISTER);
    }
}
