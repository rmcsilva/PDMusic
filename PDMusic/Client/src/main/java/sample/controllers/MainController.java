package sample.controllers;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import sample.controllers.communication.CommunicationHandler;
import sample.controllers.tabs.musicsTab.AddMusicController;
import sample.controllers.tabs.musicsTab.MusicsController;
import sample.controllers.tabs.playlistsTab.AddPlaylistController;
import sample.controllers.tabs.playlistsTab.PlaylistSelectedController;
import sample.controllers.tabs.playlistsTab.PlaylistsController;
import sample.controllers.tabs.playlistsTab.SelectMusicsController;
import sample.models.MusicViewModel;
import sample.models.PlaylistViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable, LayoutsConstants {

    @FXML
    public JFXTabPane tabContainer;

    @FXML
    public Tab musicsTab, playlistsTab;

    @FXML
    private MusicsController musicsController;
    @FXML
    private PlaylistsController playlistsController;

    private ScreenController screenController;

    private CommunicationHandler communicationHandler;

    private ObservableList<MusicViewModel> musics;
    private ObservableList<MusicViewModel> musicsInPlaylist;
    private ObservableList<MusicViewModel> musicsNotInPlaylist;

    private ObservableList<PlaylistViewModel> playlists;

    private String username;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenController = ScreenController.getInstance();

        musicsController.setMainController(this);
        playlistsController.setMainController(this);

        playlists = FXCollections.observableArrayList();

        musics = FXCollections.observableArrayList();
        musicsInPlaylist = FXCollections.observableArrayList();
        musicsNotInPlaylist = FXCollections.observableArrayList();
        setupMusicsTreeTableView(musicsController.getTtvMusics(), musics);
        setupPlaylistsTreeTableView(playlistsController.getTtvPlaylists());

        setupScreenController();
        configureTabPane();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCommunicationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
        communicationHandler.setNotificationHandlerMainController(this);
    }

    public CommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    public void addMusic(String username, String name, String author, String album, int year, int duration, String genre) {
        musics.add(new MusicViewModel(name, author, album, genre, year, duration, username));
    }

    public void addPlaylist(String username,  String name) {
        playlists.add(new PlaylistViewModel(name, username));
    }

    private void setupScreenController() {
        screenController.addScreen(ScreenController.Screen.MUSICS, (Pane) musicsTab.getContent());
        screenController.addScreen(ScreenController.Screen.PLAYLISTS, (Pane) playlistsTab.getContent());

        try {
            //Add Music Menu Layout
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_TAB_ADD_MUSIC));
            screenController.addScreen(ScreenController.Screen.ADD_MUSIC, fxmlLoader.load());
            ((AddMusicController) fxmlLoader.getController()).setMainController(this);

            //Playlist Selected Layout
            fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_TAB_PLAYLIST_SELECTED));
            screenController.addScreen(ScreenController.Screen.PLAYLIST_SELECTED, fxmlLoader.load());
            PlaylistSelectedController playlistSelectedController = fxmlLoader.getController();
            playlistSelectedController.setMainController(this);
            setupMusicsTreeTableView(playlistSelectedController.getTtvMusicsInPlaylist(), musicsInPlaylist);

            //Add Playlist Layout
            fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_TAB_ADD_PLAYLIST));
            screenController.addScreen(ScreenController.Screen.ADD_PLAYLIST, fxmlLoader.load());
            ((AddPlaylistController) fxmlLoader.getController()).setMainController(this);

            //Select Musics Layout
            fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_TAB_SELECT_MUSICS));
            screenController.addScreen(ScreenController.Screen.SELECT_MUSICS, fxmlLoader.load());
            SelectMusicsController selectMusicsController = fxmlLoader.getController();
            selectMusicsController.setMainController(this);
            setupMusicsTreeTableView(selectMusicsController.getTtvMusicsNotInPlaylist(), musicsNotInPlaylist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupMusicsTreeTableView(JFXTreeTableView<MusicViewModel> ttvMusics, ObservableList<MusicViewModel> musics) {
        JFXTreeTableColumn<MusicViewModel, String> musicName = new JFXTreeTableColumn<>(COLUMN_MUSIC_NAME);
        musicName.setCellValueFactory(param -> param.getValue().getValue().musicNameProperty());

        JFXTreeTableColumn<MusicViewModel, String> author = new JFXTreeTableColumn<>(COLUMN_AUTHOR);
        author.setCellValueFactory(param -> param.getValue().getValue().authorProperty());

        JFXTreeTableColumn<MusicViewModel, String> album = new JFXTreeTableColumn<>(COLUMN_ALBUM);
        album.setCellValueFactory(param -> param.getValue().getValue().albumProperty());

        JFXTreeTableColumn<MusicViewModel, Integer> year = new JFXTreeTableColumn<>(COLUMN_YEAR);
        year.setCellValueFactory(param -> param.getValue().getValue().yearProperty().asObject());

        JFXTreeTableColumn<MusicViewModel, Integer> duration = new JFXTreeTableColumn<>(COLUMN_DURATION);
        duration.setCellValueFactory(param -> param.getValue().getValue().durationProperty().asObject());

        JFXTreeTableColumn<MusicViewModel, String> genre = new JFXTreeTableColumn<>(COLUMN_GENRE);
        genre.setCellValueFactory(param -> param.getValue().getValue().genreProperty());

        JFXTreeTableColumn<MusicViewModel, String> username = new JFXTreeTableColumn<>(COLUMN_USERNAME);
        username.setCellValueFactory(param -> param.getValue().getValue().usernameProperty());

        //Dynamic Column Width
        musicName.prefWidthProperty().bind(ttvMusics.widthProperty().divide(NUMBER_MUSIC_COLUMNS));
        author.prefWidthProperty().bind(ttvMusics.widthProperty().divide(NUMBER_MUSIC_COLUMNS));
        album.prefWidthProperty().bind(ttvMusics.widthProperty().divide(NUMBER_MUSIC_COLUMNS));
        year.prefWidthProperty().bind(ttvMusics.widthProperty().divide(NUMBER_MUSIC_COLUMNS));
        duration.prefWidthProperty().bind(ttvMusics.widthProperty().divide(NUMBER_MUSIC_COLUMNS));
        genre.prefWidthProperty().bind(ttvMusics.widthProperty().divide(NUMBER_MUSIC_COLUMNS));
        username.prefWidthProperty().bind(ttvMusics.widthProperty().divide(NUMBER_MUSIC_COLUMNS));

        final TreeItem<MusicViewModel> root = new RecursiveTreeItem<>(musics, RecursiveTreeObject::getChildren);
        ttvMusics.getColumns().setAll(musicName, author, album, year, duration, genre, username);
        ttvMusics.setRoot(root);
        ttvMusics.setShowRoot(false);
    }

    private void setupPlaylistsTreeTableView(JFXTreeTableView<PlaylistViewModel> ttvPlaylists) {
        JFXTreeTableColumn<PlaylistViewModel, String> playlistName = new JFXTreeTableColumn<>(COLUMN_PLAYLIST_NAME);
        playlistName.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

        JFXTreeTableColumn<PlaylistViewModel, String> username = new JFXTreeTableColumn<>(COLUMN_USERNAME);
        username.setCellValueFactory(param -> param.getValue().getValue().usernameProperty());

        //Dynamic Column Width
        playlistName.prefWidthProperty().bind(ttvPlaylists.widthProperty().divide(NUMBER_PLAYLIST_COLUMNS));
        username.prefWidthProperty().bind(ttvPlaylists.widthProperty().divide(NUMBER_PLAYLIST_COLUMNS));

        final TreeItem<PlaylistViewModel> root = new RecursiveTreeItem<>(playlists, RecursiveTreeObject::getChildren);
        ttvPlaylists.getColumns().setAll(playlistName, username);
        ttvPlaylists.setRoot(root);
        ttvPlaylists.setShowRoot(false);
    }

    public void changeMusicsTab(ScreenController.Screen screen) {
        musicsTab.setContent(screenController.getPane(screen));
    }

    public void changePlaylistsTab(ScreenController.Screen screen) {
        playlistsTab.setContent(screenController.getPane(screen));
    }

    @FXML
    public void logout(MouseEvent mouseEvent) throws IOException {
        communicationHandler.logout();
    }

    private void configureTabPane() {
        tabContainer.setRotateGraphic(true);

        tabContainer.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            tabContainer.setTabMinWidth((newValue.doubleValue() / 2) - 20);
            tabContainer.setTabMaxWidth((newValue.doubleValue() / 2) - 20);
        });

        configureTab(musicsTab, TAB_MUSICS, FontAwesomeIcon.MUSIC);
        configureTab(playlistsTab, TAB_PLAYLISTS, FontAwesomeIcon.LIST_UL);
    }

    private void configureTab(Tab tab, String title, FontAwesomeIcon icon) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setGlyphSize(tabIconSize);
        iconView.setStyleClass("icons-color");

        Label label = new Label(title);
        label.setPadding(new Insets(12, 0, 0, 24));
        label.setStyle("-fx-font-size: " + tabFontSize + "pt;");
        label.setTextAlignment(TextAlignment.CENTER);

        BorderPane tabPane = new BorderPane();
        tabPane.setRotate(90.0);
        tabPane.setCenter(iconView);
        tabPane.setBottom(label);

        tab.setText("");
        tab.setGraphic(tabPane);
    }
}
