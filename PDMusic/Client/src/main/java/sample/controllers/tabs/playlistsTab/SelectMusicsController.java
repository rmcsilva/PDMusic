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

    public void addMusicNotInPlaylist(MusicViewModel music) {
        musicsNotInPlaylist.add(music);
    }

    @FXML
    void addMusicToPlaylist(ActionEvent event) {
        //TODO: Add music to playlist
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
