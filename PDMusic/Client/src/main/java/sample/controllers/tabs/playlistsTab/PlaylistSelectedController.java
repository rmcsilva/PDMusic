package sample.controllers.tabs.playlistsTab;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import sample.controllers.tabs.TabCommunication;

public class PlaylistSelectedController extends TabCommunication {

    @FXML
    private void goToPlaylistsMenu(MouseEvent event) {
        getMainController().goToPlaylistsMenu();
    }

}
