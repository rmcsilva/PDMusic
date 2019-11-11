package sample.controllers.tabs.playlistsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.json.JSONObject;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

import java.io.IOException;

public class AddPlaylistController extends TabCommunication {
    private String username = "PDMUSIC1", name = "P1", gen = "Rock";
    @FXML
    void savePlaylist(ActionEvent event) {
        //TODO: Add playlist
        JSONObject options = new JSONObject();
        options.put("tipo" , "newPlaylist");
        options.put("owner", username);
        options.put("name", name);
        options.put("genre", gen);

        try {
            geraJSON(options);
            leJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }
        goBackToPlaylistsMenu();
    }

    @FXML
    void cancelPlaylistChanges(ActionEvent event) {
        goBackToPlaylistsMenu();
    }

    private void goBackToPlaylistsMenu() {
        getMainController().changePlaylistsTab(ScreenController.Screen.PLAYLISTS);
    }

}
