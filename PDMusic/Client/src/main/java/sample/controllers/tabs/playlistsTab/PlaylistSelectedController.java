package sample.controllers.tabs.playlistsTab;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import sample.controllers.ScreenController.Screen;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistSelectedController extends TabCommunication implements Initializable {

    @FXML
    private Text playlistNameText;
    @FXML
    private JFXTextField searchMusicTextField;
    @FXML
    private JFXButton addMusicButton, removeMusicButton;

    @FXML
    private JFXTreeTableView<MusicViewModel> ttvMusicsInPlaylist;

    private ObservableList<MusicViewModel> musicsInPlaylist;

    public PlaylistSelectedController() {
        musicsInPlaylist = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchMusicTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> ttvMusicsInPlaylist.setPredicate(musicTreeItem -> musicTreeItem.getValue().predicate(newValue))
        );
    }

    public JFXTreeTableView<MusicViewModel> getTtvMusicsInPlaylist() {
        return ttvMusicsInPlaylist;
    }

    public ObservableList<MusicViewModel> getMusicsInPlaylist() {
        return musicsInPlaylist;
    }

    private MusicViewModel getSelectMusic() {
        return ttvMusicsInPlaylist.getSelectionModel().getSelectedItem().getValue();
    }

    void setMusicsInPlaylist(ObservableList<MusicViewModel> musicsInPlaylist) {
        this.musicsInPlaylist.setAll(musicsInPlaylist);
    }

    public void clearMusicsInPlaylist() {
        musicsInPlaylist.clear();
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
        String musicName = getSelectMusic().getMusicName();
        //Send request to remove music from playlist
        getMainController().getCommunicationHandler().removeMusicFromPlaylist(musicName, playlistNameText.getText());
    }

    @FXML
    private void goToPlaylistsMenu(MouseEvent event) {
        getMainController().changePlaylistsTab(Screen.PLAYLISTS);
    }

    @FXML
    public void playPlaylist(MouseEvent mouseEvent) {
        getMainController().playPlaylist(playlistNameText.getText());
    }

    @FXML
    public void playMusic(ActionEvent actionEvent) {
        if (ttvMusicsInPlaylist.getSelectionModel().getSelectedItem() == null) return;
        getMainController().setPlaylistMode(false);
        getMainController().playMusic(getSelectMusic().getMusicName());
    }
}
