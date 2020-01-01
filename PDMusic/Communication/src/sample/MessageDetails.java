package sample;

public interface MessageDetails {
    //Message Details
    //Success Messages
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
    String REMOVE_MUSIC_FROM_PLAYLIST_SUCCESS = "Music Remove From Playlist Successfully!\n";
    String LOGOUT_SUCCESS = "Logged Out Successfully!\n";
    //User Errors
    //Login
    String USERNAME_NOT_FOUND = "Can't find your PDMusic account!\n";
    String PASSWORD_MISMATCH = "Password Mismatch, try again!\n";
    //Register
    String USERNAME_ALREADY_TAKEN = "Username has already been taken!\n";
    //General
    String USER_NOT_LOGGED_IN = "User needs to be logged in to use the PDMusic functionality!\n";
    //Music
    String MUSIC_ALREADY_EXISTS = "Music name already exists!\n";
    String MUSIC_NOT_EXISTS = "Music does not exist!\n";
    String EDIT_DIFFERENT_MUSIC_OWNER = "Only the owner of the musics can edit them!\n";
    String REMOVE_DIFFERENT_MUSIC_OWNER = "Only the owner of the musics can remove them!\n";
    //Playlist
    String PLAYLIST_ALREADY_EXISTS = "Playlist name already exists!\n";
    String PLAYLIST_NOT_EXISTS = "Playlist does not exist!\n";
    String EDIT_DIFFERENT_PLAYLIST_OWNER = "Only the owner of the playlist can edit them!\n";
    String REMOVE_DIFFERENT_PLAYLIST_OWNER = "Only the owner of the playlist can remove them!\n";
    //Add Music To Playlist
    String ADD_TO_PLAYLIST_DIFFERENT_OWNER = "Only the owner of the playlist can add musics to it!\n";
    String REMOVE_FROM_PLAYLIST_DIFFERENT_OWNER = "Only the owner of the playlist can remove musics from it!\n";
}
