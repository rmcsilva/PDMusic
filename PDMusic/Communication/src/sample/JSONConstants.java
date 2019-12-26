package sample;

public interface JSONConstants {
    //Request keys
    String REQUEST = "request";

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

    //Logout request
    String REQUEST_LOGOUT = "logout";

    //Server Shutdown Request
    String SERVER_SHUTDOWN = "serverShutdown";

    //Response keys
    String RESPONSE = "response";
    //Response keys status
    String STATUS = "status";
    String APPROVED = "approved";
    String DENIED = "denied";
    //Response details
    String DETAILS = "details";
    //Message Details
    String LOGIN_SUCCESS = "Login Successful!\n";
    String REGISTER_SUCCESS = "Registered Successfully!\n";
    String ADD_MUSIC_SUCCESS = "Music Added Successfully!\n";
    String EDIT_MUSIC_SUCCESS = "Music Edited Successfully!\n";
    String REMOVE_MUSIC_SUCCESS = "Music Removed Successfully!\n";
    String GET_MUSIC_SUCCESS = "Music Ready To Begin Download!\n";
    String ADD_PLAYLIST_SUCCESS = "Playlist Added Successfully!\n";
    String EDIT_PLAYLIST_SUCCESS = "Playlist Edited Successfully!\n";
    String REMOVE_PLAYLIST_SUCCESS = "Playlist Removed Successfully!\n";
    String ADD_MUSIC_TO_PLAYLIST_SUCCESS = "Music Added To Playlist Successfully!\n";
    String LOGOUT_SUCCESS = "Logged Out Successfully!\n";

    //Notification keys
    String NOTIFICATION = "notification";


}
