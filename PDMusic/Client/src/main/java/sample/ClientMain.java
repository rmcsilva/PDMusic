package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controllers.LoginController;
import sample.controllers.ScreenController;

import java.io.IOException;

import static sample.controllers.LayoutsConstants.LAYOUT_LOGIN;

public class ClientMain extends Application {

    private LoginController loginController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        ScreenController screenController = ScreenController.getInstance();

        String serversDirectoryIP = getParameters().getRaw().get(0);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_LOGIN));
        screenController.addScreen(ScreenController.Screen.LOGIN, fxmlLoader.load());
        loginController = fxmlLoader.getController();
        loginController.setServersDirectoryIP(serversDirectoryIP);

        Parent root = screenController.getPane(ScreenController.Screen.LOGIN);
        Scene scene = new Scene(root);
        screenController.setScene(scene);

        primaryStage.setTitle("PDMusic");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Sintaxe: java Client <ServersDirectoryIP>");
            return;
        }

        launch(args);
    }

    @Override
    public void stop(){
        //Stop CommunicationHandler Thread if active
        loginController.stopCommunicationHandler();
    }
}
