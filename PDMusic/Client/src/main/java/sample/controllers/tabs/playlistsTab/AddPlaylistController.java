package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;
import sample.models.PlaylistViewModel;

public class AddPlaylistController extends TabCommunication {

    @FXML
    private JFXTextField playlistNameField;

    private boolean editPlaylist = false;

    @FXML
    void savePlaylist(ActionEvent event) {
        String playlistName = playlistNameField.getText();

        if (playlistName.isEmpty()) {
            return;
        }

        //TODO: Add playlist
        getMainController().getCommunicationHandler().addPlaylist(playlistName);

        clearFields();

        goBackToPlaylistsMenu();
    }

    @FXML
    void cancelPlaylistChanges(ActionEvent event) {
        editPlaylist = false;
        clearFields();
        goBackToPlaylistsMenu();
    }

    public void editPlaylist(PlaylistViewModel playlistViewModel) {
        editPlaylist = true;
        playlistNameField.setText(playlistViewModel.getName());
    }

    private void goBackToPlaylistsMenu() {
        getMainController().changePlaylistsTab(ScreenController.Screen.PLAYLISTS);
    }

    private void clearFields() {
        playlistNameField.clear();
    }
}
