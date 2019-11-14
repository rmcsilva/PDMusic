package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

public class AddPlaylistController extends TabCommunication {

    @FXML
    private JFXTextField playlistNameField;

    @FXML
    void savePlaylist(ActionEvent event) {
        String playlistName = playlistNameField.getText();

        if (playlistName.isEmpty()) {
            return;
        }

        //TODO: Add playlist
        getMainController().getCommunicationHandler().addPlaylist(playlistName);

        playlistNameField.clear();

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
