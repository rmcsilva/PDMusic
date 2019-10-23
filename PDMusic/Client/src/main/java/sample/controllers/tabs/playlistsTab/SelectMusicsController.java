package sample.controllers.tabs.playlistsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

public class SelectMusicsController extends TabCommunication {

    @FXML
    void addMusicToPlaylist(ActionEvent event) {
        //TODO: Add music to playlist
        goBackToSelectedPlaylist();
    }

    @FXML
    void cancelMusicSelection(ActionEvent event) {
        goBackToSelectedPlaylist();
    }

    private void goBackToSelectedPlaylist() {
        getMainController().changePlaylistsTab(ScreenController.Screen.PLAYLIST_SELECTED);
    }
}
