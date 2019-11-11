package sample.controllers.tabs.musicsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.json.JSONObject;
import sample.Communication;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddMusicController extends TabCommunication {
    private String gen = "Pop Rock", artist = "Editors", name = "Munich", album = "Munich", year = "2005";
    @FXML
    void saveMusic(ActionEvent event) {
        //TODO: Add music
        JSONObject options = new JSONObject();
        options.put("tipo" , "newMusic");
        options.put("artist", artist);
        options.put("name", name);
        options.put("genre", gen);
        options.put("album", album);
        options.put("year", year);

        try {
            geraJSON(options);
            leJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
