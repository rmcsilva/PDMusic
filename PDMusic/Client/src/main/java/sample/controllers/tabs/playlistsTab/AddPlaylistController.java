package sample.controllers.tabs.playlistsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

public class AddPlaylistController extends TabCommunication {

    @FXML
    void savePlaylist(ActionEvent event) {
        //TODO: Add playlist
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
