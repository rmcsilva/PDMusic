package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controllers.ScreenController;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ScreenController screenController = ScreenController.getInstance();

        screenController.addScreen(ScreenController.Screen.LOGIN, FXMLLoader.load(getClass().getResource("/layouts/login.fxml")));

        Parent root = screenController.getPane(ScreenController.Screen.LOGIN);
        Scene scene = new Scene(root);
        screenController.setScene(scene);

        primaryStage.setTitle("PDMusic");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
