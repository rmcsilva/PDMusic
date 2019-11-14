package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import sample.controllers.ScreenController.Screen;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;

public class PlaylistSelectedController extends TabCommunication {

    @FXML
    private JFXTreeTableView<MusicViewModel> ttvMusicsInPlaylist;

    public JFXTreeTableView<MusicViewModel> getTtvMusicsInPlaylist() {
        return ttvMusicsInPlaylist;
    }

    @FXML
    void addMusicToPlaylist(ActionEvent event) {
        getMainController().changePlaylistsTab(Screen.SELECT_MUSICS);
    }

    @FXML
    private void goToPlaylistsMenu(MouseEvent event) {
        getMainController().changePlaylistsTab(Screen.PLAYLISTS);
    }

}
