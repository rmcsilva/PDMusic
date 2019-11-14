package sample.controllers.tabs.musicsTab;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;


public class MusicsController extends TabCommunication {

    @FXML
    private JFXTreeTableView<MusicViewModel> ttvMusics;

    public JFXTreeTableView<MusicViewModel> getTtvMusics() {
        return ttvMusics;
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
