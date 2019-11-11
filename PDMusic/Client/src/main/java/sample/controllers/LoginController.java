package sample.controllers;

import com.sun.istack.internal.localization.NullLocalizable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import org.json.JSONException;
import org.json.JSONObject;
import sample.Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController extends Communication implements Initializable {

    private RegisterController registerController = null;
    private MainController mainController = null;
    private ScreenController screenController;

    //Isto é para remover
    private String username = "PDMUSIC1";
    private String password = "123456";

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

                //Adiciona os dados do user ao JSON para enviar para o servidor
                JSONObject options = new JSONObject();
                options.put("tipo" , "login");
                options.put("username", username);
                options.put("password", password);
                //Envia pedido
                GenerateJSON(options);
                //Recebe resposta
                ReadJSONFromServer();
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
