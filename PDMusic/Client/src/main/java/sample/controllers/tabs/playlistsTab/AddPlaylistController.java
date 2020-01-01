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
    private String playlistToEdit;

    private ScreenController screenController = ScreenController.getInstance();

    @FXML
    void savePlaylist(ActionEvent event) {
        String playlistName = playlistNameField.getText();

        if (playlistName.isEmpty()) {
            screenController.showDialog("Save Playlist Invalid Form", "Playlist name is required!\n");
            return;
        }

        //Check to see if it needs to send an add or edit request to the server
        if (editPlaylist) {
            getMainController().getCommunicationHandler().editPlaylist(playlistToEdit, playlistName);
        } else {
            getMainController().getCommunicationHandler().addPlaylist(playlistName);
        }

        editPlaylist = false;

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
        String playlistName = playlistViewModel.getName();
        playlistToEdit = playlistName;
        playlistNameField.setText(playlistName);
    }

    private void goBackToPlaylistsMenu() {
        getMainController().changePlaylistsTab(ScreenController.Screen.PLAYLISTS);
    }

    private void clearFields() {
        playlistNameField.clear();
    }
}
