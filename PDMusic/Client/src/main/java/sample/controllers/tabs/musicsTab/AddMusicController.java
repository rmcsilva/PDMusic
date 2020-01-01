package sample.controllers.tabs.musicsTab;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import sample.controllers.ScreenController;
import sample.controllers.communication.files.ClientFileManager;
import sample.controllers.tabs.TabCommunication;
import sample.models.MusicViewModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class AddMusicController extends TabCommunication {

    @FXML
    private JFXTextField musicNameField, authorField, albumField, yearField, durationField, genreField;

    private boolean editMusic = false;
    private String musicToEdit;

    private boolean isMusicSelected = false;
    private Path selectedMusicPath;

    private String musicName, author, album, genre;
    private int year, duration;

    private ScreenController screenController = ScreenController.getInstance();

    private final String invalidForm = "Save Music Invalid Form";

    private boolean getFieldValues() {
        if (!isMusicSelected) {
            screenController.showDialog(invalidForm, "Music file needs to be selected!\n");
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
            screenController.showDialog(invalidForm, "Year and duration need to be integers!\n");
            return false;
        }

        if (musicName.isEmpty() || author.isEmpty() || album.isEmpty() || genre.isEmpty()) {
            screenController.showDialog(invalidForm, "All fields are required!\n");
            return false;
        }

        return true;
    }

    public void editMusic(MusicViewModel musicViewModel) {
        editMusic = true;
        isMusicSelected = false;
        musicToEdit = musicViewModel.getMusicName();
        musicNameField.setText(musicViewModel.getMusicName());
        authorField.setText(musicViewModel.getAuthor());
        albumField.setText(musicViewModel.getAlbum());
        yearField.setText(String.valueOf(musicViewModel.getYear()));
        durationField.setText(String.valueOf(musicViewModel.getDuration()));
        genreField.setText(musicViewModel.getGenre());
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

        try {
            Files.copy(selectedMusicPath, Paths.get(ClientFileManager.getMusicPath(musicName)), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (editMusic) {
            getMainController().getCommunicationHandler().editMusic(musicToEdit, musicName, author, album, year, duration, genre);
        } else {
            getMainController().getCommunicationHandler().addMusic(musicName, author, album, year, duration, genre);
        }

        isMusicSelected = false;
        editMusic = false;

        clearFields();

        goBackToMusicsMenu();
    }

    @FXML
    void cancelMusicChanges(ActionEvent event) {
        isMusicSelected = false;
        editMusic = false;
        clearFields();
        goBackToMusicsMenu();
    }

    @FXML
    void selectMusic(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3","*.mp3"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            selectedMusicPath = file.toPath();
            isMusicSelected = true;
        }
    }

    private void goBackToMusicsMenu() {
        getMainController().changeMusicsTab(ScreenController.Screen.MUSICS);
    }
}
