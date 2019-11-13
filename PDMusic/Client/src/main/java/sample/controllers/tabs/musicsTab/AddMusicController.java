package sample.controllers.tabs.musicsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.json.JSONObject;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

import java.io.IOException;

public class AddMusicController extends TabCommunication {
    private String gen = "Pop Rock", artist = "Editors", name = "Munich", album = "Munich", year = "2005";
    @FXML
    void saveMusic(ActionEvent event) {
        //TODO: Add music

        goBackToMusicsMenu();
    }

    @FXML
    void cancelMusicChanges(ActionEvent event) {
        goBackToMusicsMenu();
    }

    private void goBackToMusicsMenu() {
        getMainController().changeMusicsTab(ScreenController.Screen.MUSICS);
    }
}
