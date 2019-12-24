package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;

public class SelectMusicsController extends TabCommunication {

    @FXML
    private JFXTreeTableView<MusicViewModel> ttvMusicsNotInPlaylist;

    private PlaylistsController playlistsController;

    private ObservableList<MusicViewModel> musicsNotInPlaylist;

    public SelectMusicsController() {
        musicsNotInPlaylist = FXCollections.observableArrayList();
    }

    public JFXTreeTableView<MusicViewModel> getTtvMusicsNotInPlaylist() {
        return ttvMusicsNotInPlaylist;
    }

    public ObservableList<MusicViewModel> getMusicsNotInPlaylist() {
        return musicsNotInPlaylist;
    }

    public void setMusicsNotInPlaylist(ObservableList<MusicViewModel> musicsNotInPlaylist) {
        this.musicsNotInPlaylist.setAll(musicsNotInPlaylist);
    }

    public void addMusicNotInPlaylist(MusicViewModel music) {
        musicsNotInPlaylist.add(music);
    }

    public void removeMusicNotInPlaylist(MusicViewModel music) {
        musicsNotInPlaylist.remove(music);
    }

    public void editMusic(String musicToEdit, MusicViewModel newMusic) {
        for (MusicViewModel music: musicsNotInPlaylist) {
            if (musicToEdit.equals(music.getMusicName())) {
                music = newMusic;
                return;
            }
        }
        musicsNotInPlaylist.add(newMusic);
    }

    public void setPlaylistsController(PlaylistsController playlistsController) {
        this.playlistsController = playlistsController;
    }

    @FXML
    void addMusicToPlaylist(ActionEvent event) {
        //Check if music is selected
        if (ttvMusicsNotInPlaylist.getSelectionModel().getSelectedItem() == null) return;
        //Get selected playlist musics
        MusicViewModel selectedMusic = ttvMusicsNotInPlaylist.getSelectionModel().getSelectedItem().getValue();
        String musicName = selectedMusic.getMusicName();
        String playlistName = playlistsController.getSelectedPlaylistName();
        //Check if a playlist is selected
        if (playlistName == null) return;
        //Send request to add music to playlist
        getMainController().getCommunicationHandler().addMusicToPlaylist(musicName, playlistName);
        goBackToSelectedPlaylist();
    }

    @FXML
    void cancelMusicSelection(ActionEvent event) {
        goBackToSelectedPlaylist();
    }

    private void goBackToSelectedPlaylist() {
        getMainController().changePlaylistsTab(ScreenController.Screen.PLAYLIST_SELECTED);
    }
}
