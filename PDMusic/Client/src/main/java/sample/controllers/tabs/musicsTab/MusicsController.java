package sample.controllers.tabs.musicsTab;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import sample.controllers.ScreenController;
import sample.controllers.communication.files.ClientFileManager;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;

import java.net.URL;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;


public class MusicsController extends TabCommunication implements Initializable {

    private AddMusicController addMusicController;

    @FXML
    private JFXButton editMusicButton, removeMusicButton;

    @FXML
    private JFXTreeTableView<MusicViewModel> ttvMusics;

    private ObservableList<MusicViewModel> musics;

    public MusicsController() {
        musics = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ttvMusics.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (ttvMusics.getSelectionModel().getSelectedItem() != null) {
                String musicUsername = getSelectedMusic().getUsername();
                String username = getMainController().getUsername();
                //Check if current user can edit or remove the current music
                if (musicUsername.equals(username)) {
                    enableEditAndRemoveButtons();
                } else {
                    disableEditAndRemoveButtons();
                }
            } else {
                disableEditAndRemoveButtons();
            }
        });
    }

    public JFXTreeTableView<MusicViewModel> getTtvMusics() {
        return ttvMusics;
    }

    public ObservableList<MusicViewModel> getMusics() {
        return musics;
    }

    public void addMusic(MusicViewModel music) {
        musics.add(music);
    }

    public void editMusic(String musicToEdit, MusicViewModel newMusic) {
        for (MusicViewModel music : musics) {
            if (musicToEdit.equals(music.getMusicName())) {
                music.replace(newMusic);
                return;
            }
        }
    }

    public void removeMusic(String musicToRemove) {
        for (MusicViewModel music : musics) {
            if (musicToRemove.equals(music.getMusicName())) {
                musics.remove(music);
                return;
            }
        }
    }

    public void setAddMusicController(AddMusicController addMusicController) {
        this.addMusicController = addMusicController;
    }

    public MusicViewModel getMusicByName(String musicName) throws NoSuchElementException {
        for (MusicViewModel music : musics) {
            if (music.getMusicName().equals(musicName)) return music;
        }
        throw new NoSuchElementException("Music not found");
    }

    private void enableEditAndRemoveButtons() {
        removeMusicButton.setDisable(false);
        editMusicButton.setDisable(false);
    }

    private void disableEditAndRemoveButtons() {
        removeMusicButton.setDisable(true);
        editMusicButton.setDisable(true);
    }

    private MusicViewModel getSelectedMusic() {
        return ttvMusics.getSelectionModel().getSelectedItem().getValue();
    }

    @FXML
    void addMusicMenu(ActionEvent event) {
        getMainController().changeMusicsTab(ScreenController.Screen.ADD_MUSIC);
    }

    @FXML
    void editMusicMenu(ActionEvent event) {
        if (ttvMusics.getSelectionModel().getSelectedItem() == null) return;
        addMusicController.editMusic(getSelectedMusic());
        getMainController().changeMusicsTab(ScreenController.Screen.ADD_MUSIC);
    }

    @FXML
    public void removeMusicButton(ActionEvent actionEvent) {
        if (ttvMusics.getSelectionModel().getSelectedItem() == null) return;
        getMainController().getCommunicationHandler().removeMusic(getSelectedMusic().getMusicName());
    }

    @FXML
    void playMusic(ActionEvent actionEvent) {
        if (ttvMusics.getSelectionModel().getSelectedItem() == null) return;
        //Get selected music
        MusicViewModel selectedMusic = getSelectedMusic();
        String musicName = selectedMusic.getMusicName();
        //Check if music is available to be played
        if (!ClientFileManager.isMusicAvailable(musicName)) {
            //Download music from server
            getMainController().getCommunicationHandler().getMusic(musicName);
            //TODO: Show notification that music is downloading
            return;
        }

        //Play music
        getMainController().playMusic(musicName);
    }
}
