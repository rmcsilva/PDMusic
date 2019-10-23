package sample.controllers.tabs.musicsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

public class MusicsController extends TabCommunication {

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
