package sample.controllers.tabs.musicsTab;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sample.controllers.ScreenController;
import sample.controllers.tabs.TabCommunication;


public class AddMusicController extends TabCommunication {

    @FXML
    private JFXTextField musicNameField, authorField, albumField, yearField, durationField, genreField;

    private boolean isMusicSelected = false;

    private String musicName, author, album, genre;
    private int year, duration;

    private boolean getFieldValues() {
        if (!isMusicSelected) {
            return false;
        }

        musicName = musicNameField.getText();
        author = authorField.getText();
        album = albumField.getText();
        genre = genreField.getText();

        try {
            year = Integer.parseInt(yearField.getText());
            duration = Integer.parseInt(durationField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Year and duration need to be integers!");
            return false;
        }

        if (musicName.isEmpty() || author.isEmpty() || album.isEmpty() || genre.isEmpty()) {
            return false;
        }

        return true;
    }

    private void clearFields() {
        musicNameField.clear();
        authorField.clear();
        albumField.clear();
        yearField.clear();
        durationField.clear();
        genreField.clear();
    }

    @FXML
    void saveMusic(ActionEvent event) {

        if (!getFieldValues()) {
            return;
        }

        //TODO: Add music
        getMainController().getCommunicationHandler().addMusic(musicName, author, album, year, duration, genre);

        isMusicSelected = false;

        clearFields();

        goBackToMusicsMenu();
    }

    @FXML
    void cancelMusicChanges(ActionEvent event) {
        isMusicSelected = false;
        goBackToMusicsMenu();
    }

    @FXML
    void selectMusic(ActionEvent event) {
        //TODO: Select music
        isMusicSelected = true;
    }

    private void goBackToMusicsMenu() {
        getMainController().changeMusicsTab(ScreenController.Screen.MUSICS);
    }
}
