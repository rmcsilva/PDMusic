package sample.controllers.tabs.musicsTab;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;

import java.util.NoSuchElementException;


public class MusicsController extends TabCommunication {

    private AddMusicController addMusicController;

    @FXML
    private JFXTreeTableView<MusicViewModel> ttvMusics;

    private ObservableList<MusicViewModel> musics;

    public MusicsController() {
        musics = FXCollections.observableArrayList();
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

    public void setAddMusicController(AddMusicController addMusicController) {
        this.addMusicController = addMusicController;
    }

    public MusicViewModel getMusicByName(String musicName) throws NoSuchElementException {
        for (MusicViewModel music : musics) {
            if (music.getMusicName().equals(musicName)) return music;
        }
        throw new NoSuchElementException("Music not found");
    }

    @FXML
    void addMusicMenu(ActionEvent event) {
        getMainController().changeMusicsTab(ScreenController.Screen.ADD_MUSIC);
    }

    @FXML
    void editMusicMenu(ActionEvent event) {
        //TODO: Pass data
        getMainController().changeMusicsTab(ScreenController.Screen.ADD_MUSIC);
    }



}
