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
    String REQUEST_ADD_MUSIC = "addMusic";
    String MUSIC_NAME = "musicName";
    String AUTHOR = "author";
    String ALBUM = "album";
    String YEAR = "year";
    String DURATION = "duration";
    String GENRE = "genre";

    //Playlist request keys
    String REQUEST_ADD_PLAYLIST = "addPlaylist";
    String PLAYLIST_NAME = "playlistName";

    //Logout request
    String REQUEST_LOGOUT = "logout";

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
    String ADD_MUSIC_SUCCESS = "Music Added Successfully\n";
    String ADD_PLAYLIST_SUCCESS = "Playlist Added Successfully\n";
    String LOGOUT_SUCCESS = "Logged Out Successfully\n";

}
