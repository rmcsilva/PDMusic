package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXButton;
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

    @FXML
    public Text playlistNameText;
    @FXML
    public JFXButton addMusicButton, removeMusicButton;

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

    void removeMusicFromCurrentPlaylist(MusicViewModel musicToRemove) {
        musicsInPlaylist.remove(musicToRemove);
    }

    public void editMusic(String musicToEdit, MusicViewModel newMusic) {
        for (MusicViewModel music: musicsInPlaylist) {
            if (musicToEdit.equals(music.getMusicName())) {
                music.replace(newMusic);
                return;
            }
        }
    }

    public void removeMusic(String musicToRemove) {
        for (MusicViewModel music: musicsInPlaylist) {
            if (musicToRemove.equals(music.getMusicName())) {
                musicsInPlaylist.remove(music);
                return;
            }
        }
    }

    public void setPlaylistName(String playlistName) {
        playlistNameText.setText(playlistName);
    }

    protected void showAddAndRemoveButtons() {
        addMusicButton.setVisible(true);
        removeMusicButton.setVisible(true);
    }

    protected void hideAddAndRemoveButtons() {
        addMusicButton.setVisible(false);
        removeMusicButton.setVisible(false);
    }

    @FXML
    void addMusicToPlaylistScreen(ActionEvent event) {
        getMainController().changePlaylistsTab(Screen.SELECT_MUSICS);
    }

    @FXML
    public void removeMusicFromPlaylist(ActionEvent actionEvent) {
        //Check if music is selected
        if (ttvMusicsInPlaylist.getSelectionModel().getSelectedItem() == null) return;
        String musicName = ttvMusicsInPlaylist.getSelectionModel().getSelectedItem().getValue().getMusicName();
        //Send request to remove music from playlist
        getMainController().getCommunicationHandler().removeMusicFromPlaylist(musicName, playlistNameText.getText());
    }

    @FXML
    private void goToPlaylistsMenu(MouseEvent event) {
        getMainController().changePlaylistsTab(Screen.PLAYLISTS);
    }
}
