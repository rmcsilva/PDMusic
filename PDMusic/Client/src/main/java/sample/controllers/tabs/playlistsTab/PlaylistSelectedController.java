package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import sample.controllers.ScreenController.Screen;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;

public class PlaylistSelectedController extends TabCommunication {

    public Text playlistNameText;
    @FXML
    private JFXTreeTableView<MusicViewModel> ttvMusicsInPlaylist;

    private ObservableList<MusicViewModel> musicsInPlaylist;

    public PlaylistSelectedController() {
        musicsInPlaylist = FXCollections.observableArrayList();
    }

    public JFXTreeTableView<MusicViewModel> getTtvMusicsInPlaylist() {
        return ttvMusicsInPlaylist;
    }

    public ObservableList<MusicViewModel> getMusicsInPlaylist() {
        return musicsInPlaylist;
    }

    void setMusicsInPlaylist(ObservableList<MusicViewModel> musicsInPlaylist) {
        this.musicsInPlaylist.setAll(musicsInPlaylist);
    }

    void addMusicToCurrentPlaylist(MusicViewModel music) {
        musicsInPlaylist.add(music);
    }

    public void setPlaylistName(String playlistName) {
        playlistNameText.setText(playlistName);
    }

    @FXML
    void addMusicToPlaylistScreen(ActionEvent event) {
        getMainController().changePlaylistsTab(Screen.SELECT_MUSICS);
    }

    @FXML
    private void goToPlaylistsMenu(MouseEvent event) {
        getMainController().changePlaylistsTab(Screen.PLAYLISTS);
    }
}
