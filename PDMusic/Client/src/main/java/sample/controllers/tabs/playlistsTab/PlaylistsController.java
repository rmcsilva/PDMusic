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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlaylistsController extends TabCommunication {

    private AddPlaylistController addPlaylistController;
    private PlaylistSelectedController playlistSelectedController;
    private SelectMusicsController selectMusicsController;

    @FXML
    private JFXTreeTableView<PlaylistViewModel> ttvPlaylists;

    private ObservableList<PlaylistViewModel> playlists;

    private String selectedPlaylistKey = null;
    private Map<String, ObservableList<MusicViewModel>> playlistMusics;

    public PlaylistsController() {
        playlists = FXCollections.observableArrayList();
        playlistMusics = new HashMap<>();
    }

    public JFXTreeTableView<PlaylistViewModel> getTtvPlaylists() {
        return ttvPlaylists;
    }

    public ObservableList<PlaylistViewModel> getPlaylists() {
        return playlists;
    }

    public void addPlaylist(PlaylistViewModel playlist) {
        playlists.add(playlist);
        playlistMusics.put(playlist.getName(), FXCollections.observableArrayList());
    }

    public void addMusicToPlaylist(String playlistName, MusicViewModel music) {
        //Check if playlist exists
        if (!playlistMusics.containsKey(playlistName)) return;
        //Get the musics that are in that playlist
        ObservableList<MusicViewModel> musics = playlistMusics.get(playlistName);
        musics.add(music);
        //If the playlist is selected add the music to it and remove it from the selects music controller
        if (selectedPlaylistKey != null && selectedPlaylistKey.equals(playlistName)) {
            playlistSelectedController.addMusicToCurrentPlaylist(music);
            selectMusicsController.removeMusicNotInPlaylist(music);
        }
    }

    String getSelectedPlaylistName() {
        return selectedPlaylistKey;
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
        if (ttvPlaylists.getSelectionModel().getSelectedItem() == null) return;
        //Get selected playlist musics
        PlaylistViewModel selectedPlaylist = ttvPlaylists.getSelectionModel().getSelectedItem().getValue();
        //Only change values if the playlist is different than the one that is selected
        if (selectedPlaylistKey == null || !selectedPlaylistKey.equals(selectedPlaylist.getName())){
            selectedPlaylistKey = selectedPlaylist.getName();
            ObservableList<MusicViewModel> musicsInPlaylist = playlistMusics.get(selectedPlaylistKey);
            //Update content
            playlistSelectedController.setMusicsInPlaylist(musicsInPlaylist);
            setupMusicsNotInPlaylist(getMainController().getMusics(), musicsInPlaylist);
        }
        //Change tab
        getMainController().changePlaylistsTab(ScreenController.Screen.PLAYLIST_SELECTED);
    }

    private void setupMusicsNotInPlaylist(ObservableList<MusicViewModel> musics, ObservableList<MusicViewModel> musicsInPlaylist) {
        //If there are no musics in playlist just add all the available musics to be selected
        if (musicsInPlaylist.isEmpty()) {
            selectMusicsController.setMusicsNotInPlaylist(musics);
            return;
        }
        // Prepare a union
        Set<MusicViewModel> union = new HashSet<>(musics);
        union.addAll(musicsInPlaylist);
        // Prepare an intersection
        Set<MusicViewModel> intersection = new HashSet<>(musics);
        intersection.retainAll(musicsInPlaylist);
        // Subtract the intersection from the union
        union.removeAll(intersection);
        // Set the selected musics to ones that are unique
        selectMusicsController.setMusicsNotInPlaylist(FXCollections.observableArrayList(union));
    }

}
