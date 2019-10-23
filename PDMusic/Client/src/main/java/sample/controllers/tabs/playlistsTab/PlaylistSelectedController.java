package sample.controllers.tabs.playlistsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import sample.controllers.ScreenController.Screen;
import sample.controllers.tabs.TabCommunication;

public class PlaylistSelectedController extends TabCommunication {

    @FXML
    void addMusicToPlaylist(ActionEvent event) {
        getMainController().changePlaylistsTab(Screen.SELECT_MUSICS);
    }

    @FXML
    private void goToPlaylistsMenu(MouseEvent event) {
        getMainController().changePlaylistsTab(Screen.PLAYLISTS);
    }

}
