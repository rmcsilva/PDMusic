package sample.controllers.tabs.playlistsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

public class PlaylistsController extends TabCommunication {

    @FXML
    void addPlaylistMenu(ActionEvent event) {
        goToAddPlaylistMenu();
    }

    @FXML
    void editPlaylistMenu(ActionEvent event) {
        //TODO: Pass data
        goToAddPlaylistMenu();
    }

    private void goToAddPlaylistMenu() {
        getMainController().changePlaylistsTab(ScreenController.Screen.ADD_PLAYLIST);
    }

    @FXML
    void selectPlaylist(ActionEvent event) {
        getMainController().changePlaylistsTab(ScreenController.Screen.PLAYLIST_SELECTED);
    }

}
