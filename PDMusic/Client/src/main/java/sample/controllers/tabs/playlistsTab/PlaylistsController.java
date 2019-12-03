package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;
import sample.models.PlaylistViewModel;

import java.util.HashMap;
import java.util.Map;

public class PlaylistsController extends TabCommunication {

    private AddPlaylistController addPlaylistController;
    private PlaylistSelectedController playlistSelectedController;
    private SelectMusicsController selectMusicsController;

    @FXML
    private JFXTreeTableView<PlaylistViewModel> ttvPlaylists;

    private ObservableList<PlaylistViewModel> playlists;

    public PlaylistsController() {
        playlists = FXCollections.observableArrayList();
    }

    public JFXTreeTableView<PlaylistViewModel> getTtvPlaylists() {
        return ttvPlaylists;
    }

    public ObservableList<PlaylistViewModel> getPlaylists() {
        return playlists;
    }

    public void addPlaylist(PlaylistViewModel playlist) {
        playlists.add(playlist);
    }

    public void setAddPlaylistController(AddPlaylistController addPlaylistController) {
        this.addPlaylistController = addPlaylistController;
    }

    public void setPlaylistSelectedController(PlaylistSelectedController playlistSelectedController) {
        this.playlistSelectedController = playlistSelectedController;
    }

    public void setSelectMusicsController(SelectMusicsController selectMusicsController) {
        this.selectMusicsController = selectMusicsController;
    }

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
