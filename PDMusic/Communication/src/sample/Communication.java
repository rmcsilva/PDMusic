package sample;

import java.io.IOException;

public interface Communication extends JSONConstants {

    void login(String username, String password) throws IOException;
    void register(String name, String username,String password) throws IOException;
    void addMusic(String name, String author, String album, int year, int duration, String genre);
    void editMusic(String musicToEdit, String name, String author, String album, int year, int duration, String genre);
    void getMusic(String musicName);
    void addPlaylist(String name);
    void addMusicToPlaylist(String musicName, String playlistName);
    void logout();

}
