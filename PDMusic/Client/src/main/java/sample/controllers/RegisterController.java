package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import sample.Communication;

import java.io.IOException;

public class RegisterController extends Communication {

    private String username = "PDMUSIC1", password = "123456", name = "Richard Mille";
    LoginController loginController;

    public void setLoginController(LoginController loginController) { this.loginController = loginController; }

    private void goToLogin() { ScreenController.getInstance().activate(ScreenController.Screen.LOGIN); }

    @FXML
    void confirmRegistration(ActionEvent event) {
        //TODO: Send data back to login
        //Adiciona os dados do user ao JSON para enviar para o servidor
        JSONObject options = new JSONObject();
        options.put("tipo" , "register");
        options.put("name", name);
        options.put("username", username);
        options.put("password", password);
        //Envia pedido
        try {
            GenerateJSON(options);
            //Recebe resposta
            ReadJSONFromServer();
        }catch (IOException e){
            e.printStackTrace();
        }
        goToLogin();
    }

    @FXML
    void cancelRegistration(MouseEvent event) {
        goToLogin();
    }
}
