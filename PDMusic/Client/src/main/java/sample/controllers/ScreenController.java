package sample.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.EnumMap;

public class ScreenController {

    public enum Screen {
        LOGIN,
        REGISTER,
        MAIN,
        MUSICS,
        ADD_MUSIC,
        PLAYLISTS,
        ADD_PLAYLIST,
        PLAYLIST_SELECTED,
        SELECT_MUSICS
    }

    private static ScreenController screenController = null;

    private EnumMap<Screen, Pane> screenMap = new EnumMap<>(Screen.class);
    private Scene main;
    private Pane currentPane;

    public static ScreenController getInstance()
    {
        // To ensure only one instance is created
        if (screenController == null)
            screenController = new ScreenController();

        return screenController;
    }

    protected Scene getScene() { return main; }

    public void setScene(Scene main) {
        this.main = main;
    }

    public void addScreen(Screen screen, Pane pane){
        screenMap.put(screen, pane);
    }

    public void removeScreen(Screen screen){
        screenMap.remove(screen);
    }

    public Pane getPane(Screen screen) {
        return screenMap.get(screen);
    }

    public synchronized void activate(Screen screen){
        currentPane = screenMap.get(screen);
        main.setRoot(currentPane);
    }

    public synchronized void showDialog(String heading, String body) {
        JFXDialogLayout content = new JFXDialogLayout();

        Text headingText = new Text(heading);
        headingText.getStyleClass().add("headingText");
        content.setHeading(headingText);

        Text bodyText = new Text(body);
        bodyText.getStyleClass().add("bodyText");
        content.setBody(bodyText);

        JFXDialog dialog = new JFXDialog((StackPane)currentPane, content, JFXDialog.DialogTransition.CENTER);

        JFXButton confirmBtn = new JFXButton("Confirm");
        confirmBtn.addEventHandler(ActionEvent.ACTION, (e)-> {
            dialog.close();
        });

        content.setActions(confirmBtn);

        dialog.show();
    }
}
