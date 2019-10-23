package sample.controllers.tabs.musicsTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;

public class AddMusicController extends TabCommunication {

    @FXML
    void saveMusic(ActionEvent event) {
        //TODO: Add music
        goBackToMusicsMenu();
    }

    @FXML
    void cancelMusicChanges(ActionEvent event) {
        goBackToMusicsMenu();
    }

    private void goBackToMusicsMenu() {
        getMainController().changeMusicsTab(ScreenController.Screen.MUSICS);
    }
}
