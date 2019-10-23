package sample.controllers;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

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

    public void activate(Screen screen){
        main.setRoot(screenMap.get(screen));
    }
}
