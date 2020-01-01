package sample;

public interface JSONConstants {
    //Request keys
    String REQUEST = "request";

    //Invalid Request
    String INVALID_REQUEST = "invalidRequest";

    //Login/Register request keys
    String REQUEST_LOGIN = "login";
    String REQUEST_REGISTER = "register";
    String NAME = "name";
    String USERNAME = "username";
    String PASSWORD = "password";

    //Music request keys
    //Add Music
    String REQUEST_ADD_MUSIC = "addMusic";
    String MUSIC_NAME = "musicName";
    String AUTHOR = "author";
    String ALBUM = "album";
    String YEAR = "year";
    String DURATION = "duration";
    String GENRE = "genre";
    //Edit Music
    String REQUEST_EDIT_MUSIC = "editMusic";
    String MUSIC_TO_EDIT = "musicToEdit";
    //Remove Music
    String REQUEST_REMOVE_MUSIC = "removeMusic";

    //Get Music request keys
    String REQUEST_GET_MUSIC = "getMusic";
    String PORT = "musicPort";

    //Playlist request keys
    //Add Playlist
    String REQUEST_ADD_PLAYLIST = "addPlaylist";
    String PLAYLIST_NAME = "playlistName";
    //Edit Playlist
    String REQUEST_EDIT_PLAYLIST = "editPlaylist";
    String PLAYLIST_TO_EDIT = "playlistToEdit";
    //Remove Playlist
    String REQUEST_REMOVE_PLAYLIST = "removePlaylist";

    //Add Music To Playlist request keys
    String REQUEST_ADD_MUSIC_TO_PLAYLIST = "addMusicToPlaylist";
    //Remove Music From Playlist
    String REQUEST_REMOVE_MUSIC_FROM_PLAYLIST = "removeMusicFromPlaylist";

    //Logout request
    String REQUEST_LOGOUT = "logout";

    //Server Shutdown Request
    String SERVER_SHUTDOWN = "serverShutdown";

    //Database Data Keys
    String MUSICS_DATA = "musicData";
    String PLAYLISTS_DATA = "playlistData";
    String MUSICS_IN_PLAYLIST_DATA = "musicsInPlaylistData";

    //Response keys
    String RESPONSE = "response";
    //Response keys status
    String STATUS = "status";
    String APPROVED = "approved";
    String DENIED = "denied";
    //Response details
    String DETAILS = "details";

    //Notification keys
    String NOTIFICATION = "notification";
}
