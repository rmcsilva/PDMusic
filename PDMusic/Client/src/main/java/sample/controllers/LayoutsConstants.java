package sample.controllers;

public interface LayoutsConstants {
    //Layout paths
    String LAYOUT_LOGIN = "/layouts/login.fxml";
    String LAYOUT_REGISTER = "/layouts/register.fxml";
    String LAYOUT_MAIN_MENU = "/layouts/mainMenu.fxml";
    //Layout tabs paths
    String LAYOUT_TAB_ADD_MUSIC = "/layouts/musicsTab/addMusicMenu.fxml";
    String LAYOUT_TAB_PLAYLIST_SELECTED = "/layouts/playlistsTab/playlistSelected.fxml";
    String LAYOUT_TAB_ADD_PLAYLIST = "/layouts/playlistsTab/addPlaylistMenu.fxml";
    String LAYOUT_TAB_SELECT_MUSICS = "/layouts/playlistsTab/selectMusics.fxml";
    //Tabs name
    String TAB_MUSICS = "Musics";
    String TAB_PLAYLISTS = "Playlists";
    //Tab sizes
    double tabIconSize = 144.0;
    int tabFontSize = 18;
    //Tree Table View Column Names
    //Musics Columns
    int NUMBER_MUSIC_COLUMNS = 7;
    String COLUMN_MUSIC_NAME = "Name";
    String COLUMN_AUTHOR = "Author";
    String COLUMN_ALBUM = "Album";
    String COLUMN_YEAR = "Year";
    String COLUMN_DURATION = "Duration";
    String COLUMN_GENRE = "Genre";
    //Playlist Columns
    int NUMBER_PLAYLIST_COLUMNS = 2;
    String COLUMN_PLAYLIST_NAME = "Name";
    //Same in both
    String COLUMN_USERNAME = "Username";
}
