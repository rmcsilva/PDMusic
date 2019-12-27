package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;
import sample.models.PlaylistViewModel;

import java.net.URL;
import java.util.*;

public class PlaylistsController extends TabCommunication implements Initializable {

    private AddPlaylistController addPlaylistController;
    private PlaylistSelectedController playlistSelectedController;
    private SelectMusicsController selectMusicsController;

    @FXML
    private JFXButton removePlaylistButton, editPlaylistButton;
    @FXML
    private JFXTextField searchPlaylistTextField;

    @FXML
    private JFXTreeTableView<PlaylistViewModel> ttvPlaylists;

    private ObservableList<PlaylistViewModel> playlists;

    private String selectedPlaylistKey = null;
    private Map<String, ObservableList<MusicViewModel>> playlistMusics;

    public PlaylistsController() {
        playlists = FXCollections.observableArrayList();
        playlistMusics = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ttvPlaylists.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(ttvPlaylists.getSelectionModel().getSelectedItem() != null) {
                String playlistUsername = getSelectedPlaylist().getUsername();
                String username = getMainController().getUsername();
                //Check if current user can edit or remove the current music
                if (playlistUsername.equals(username)) {
                    enableEditAndRemoveButtons();
                } else {
                    disableEditAndRemoveButtons();
                }
            } else {
                disableEditAndRemoveButtons();
            }
        });

        searchPlaylistTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> ttvPlaylists.setPredicate(playlistTreeItem -> playlistTreeItem.getValue().predicate(newValue))
        );
    }

    public JFXTreeTableView<PlaylistViewModel> getTtvPlaylists() {
        return ttvPlaylists;
    }

    public ObservableList<PlaylistViewModel> getPlaylists() {
        return playlists;
    }

    private PlaylistViewModel getSelectedPlaylist() {
        return ttvPlaylists.getSelectionModel().getSelectedItem().getValue();
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

    public void editPlaylist(String playlistToEdit, String newPlaylistName) {
        for(PlaylistViewModel playlist: playlists) {
            if (playlistToEdit.equals(playlist.getName())) {
                playlist.setName(newPlaylistName);
                //Change playlist key
                playlistMusics.put(newPlaylistName, playlistMusics.remove(playlistToEdit));
                if (playlistToEdit.equals(selectedPlaylistKey)) {
                    selectedPlaylistKey = newPlaylistName;
                    changeSelectedPlaylistName();
                }
                return;
            }
        }
    }

    public void removePlaylist(String playlistToRemove) {
        for(PlaylistViewModel playlist: playlists) {
            if (playlistToRemove.equals(playlist.getName())) {
                playlists.remove(playlist);
                //Remove playlist key
                playlistMusics.remove(playlistToRemove);
                if (playlistToRemove.equals(selectedPlaylistKey)) {
                    Platform.runLater(() -> getMainController().changePlaylistsTab(ScreenController.Screen.PLAYLISTS));
                    selectedPlaylistKey = null;
                }
                return;
            }
        }
    }

    public void removeMusicFromPlaylist(String playlistName, MusicViewModel musicToRemove) {
        //Check if playlist exists
        if (!playlistMusics.containsKey(playlistName)) return;
        //Get the musics that are in that playlist
        ObservableList<MusicViewModel> musics = playlistMusics.get(playlistName);
        musics.remove(musicToRemove);
        //If the playlist is selected remove the music from it and add it to the selects music controller
        if (selectedPlaylistKey != null && selectedPlaylistKey.equals(playlistName)) {
            playlistSelectedController.removeMusicFromCurrentPlaylist(musicToRemove);
            selectMusicsController.addMusicNotInPlaylist(musicToRemove);
        }
    }

    String getSelectedPlaylistKey() {
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

    private void enableEditAndRemoveButtons() {
        removePlaylistButton.setDisable(false);
        editPlaylistButton.setDisable(false);
    }

    private void disableEditAndRemoveButtons() {
        removePlaylistButton.setDisable(true);
        editPlaylistButton.setDisable(true);
    }

    private void changeSelectedPlaylistName() {
        playlistSelectedController.setPlaylistName(selectedPlaylistKey);
    }

    @FXML
    void addPlaylistMenu(ActionEvent event) {
        goToAddPlaylistMenu();
    }

    @FXML
    void editPlaylistMenu(ActionEvent event) {
        if (ttvPlaylists.getSelectionModel().getSelectedItem() == null) return;
        addPlaylistController.editPlaylist(getSelectedPlaylist());
        goToAddPlaylistMenu();
    }

    @FXML
    public void removePlaylistButton(ActionEvent actionEvent) {
        if (ttvPlaylists.getSelectionModel().getSelectedItem() == null) return;
        getMainController().getCommunicationHandler().removePlaylist(getSelectedPlaylist().getName());
    }

    private void goToAddPlaylistMenu() {
        getMainController().changePlaylistsTab(ScreenController.Screen.ADD_PLAYLIST);
    }

    @FXML
    void selectPlaylist(ActionEvent event) {
        if (ttvPlaylists.getSelectionModel().getSelectedItem() == null) return;
        //Get selected playlist musics
        PlaylistViewModel selectedPlaylist = getSelectedPlaylist();
        //Only change values if the playlist is different than the one that is selected
        if (selectedPlaylistKey == null || !selectedPlaylistKey.equals(selectedPlaylist.getName())){
            selectedPlaylistKey = selectedPlaylist.getName();
            ObservableList<MusicViewModel> musicsInPlaylist = playlistMusics.get(selectedPlaylistKey);
            //Update content
            changeSelectedPlaylistName();
            //Setup Musics
            playlistSelectedController.setMusicsInPlaylist(musicsInPlaylist);
            setupMusicsNotInPlaylist(getMainController().getMusics(), musicsInPlaylist);
            //Setup Buttons
            if (selectedPlaylist.getUsername().equals(getUsername())) {
                playlistSelectedController.showAddAndRemoveButtons();
            } else {
                playlistSelectedController.hideAddAndRemoveButtons();
            }
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
