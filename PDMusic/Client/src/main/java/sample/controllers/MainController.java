package sample.controllers;

import com.jfoenix.controls.JFXTabPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import sample.controllers.tabs.musicsTab.*;
import sample.controllers.tabs.playlistsTab.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    boolean cenas = false;

    @FXML
    public JFXTabPane tabContainer;

    @FXML
    public Tab musicsTab;
    @FXML
    public Tab playlistsTab;

    @FXML
    private MusicsController musicsController;
    @FXML
    private PlaylistsController playlistsController;

    private ScreenController screenController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenController = ScreenController.getInstance();

        musicsController.setMainController(this);
        playlistsController.setMainController(this);

        setupScreenController();
        configureTabPane();
    }

    private void setupScreenController() {
        screenController.addScreen(ScreenController.Screen.MUSICS, (Pane) musicsTab.getContent());
        screenController.addScreen(ScreenController.Screen.PLAYLISTS, (Pane) playlistsTab.getContent());

        try {
            //Add Music Menu Layout
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/musicsTab/addMusicMenu.fxml"));
            screenController.addScreen(ScreenController.Screen.ADD_MUSIC, fxmlLoader.load());
            ((AddMusicController) fxmlLoader.getController()).setMainController(this);

            //Playlist Selected Layout
            fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/playlistsTab/playlistSelected.fxml"));
            screenController.addScreen(ScreenController.Screen.PLAYLIST_SELECTED, fxmlLoader.load());
            ((PlaylistSelectedController) fxmlLoader.getController()).setMainController(this);

            //Add Playlist Layout
            fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/playlistsTab/addPlaylistMenu.fxml"));
            screenController.addScreen(ScreenController.Screen.ADD_PLAYLIST, fxmlLoader.load());
            ((AddPlaylistController) fxmlLoader.getController()).setMainController(this);

            //Select Musics Layout
            fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/playlistsTab/selectMusics.fxml"));
            screenController.addScreen(ScreenController.Screen.SELECT_MUSICS, fxmlLoader.load());
            ((SelectMusicsController) fxmlLoader.getController()).setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeMusicsTab(ScreenController.Screen screen) {
        musicsTab.setContent(screenController.getPane(screen));
    }

    public void changePlaylistsTab(ScreenController.Screen screen) {
        playlistsTab.setContent(screenController.getPane(screen));
    }

    @FXML
    public void logout(MouseEvent mouseEvent) throws IOException {
        //TODO: Send logout request
        screenController.activate(ScreenController.Screen.LOGIN);
    }

    private void configureTabPane() {
        tabContainer.setRotateGraphic(true);

        tabContainer.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            tabContainer.setTabMinWidth((newValue.doubleValue() / 2) - 20);
            tabContainer.setTabMaxWidth((newValue.doubleValue() / 2) - 20);
        });

        configureTab(musicsTab, "Musics", FontAwesomeIcon.MUSIC);
        configureTab(playlistsTab, "Playlists", FontAwesomeIcon.LIST_UL);
    }

    private void configureTab(Tab tab, String title, FontAwesomeIcon icon) {
        double iconSize = 144.0;

        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setGlyphSize(iconSize);
        iconView.setStyleClass("icons-color");

        Label label = new Label(title);
        label.setPadding(new Insets(12, 0, 0, 24));
        label.setStyle("-fx-font-size: 18pt;");
        label.setTextAlignment(TextAlignment.CENTER);

        BorderPane tabPane = new BorderPane();
        tabPane.setRotate(90.0);
        tabPane.setCenter(iconView);
        tabPane.setBottom(label);

        tab.setText("");
        tab.setGraphic(tabPane);
    }
}
