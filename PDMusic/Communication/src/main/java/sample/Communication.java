package sample;

import java.io.IOException;

public interface Communication extends JSONConstants {

    void login(String username, String password) throws IOException;
    void register(String name, String username,String password) throws IOException;
    void addMusic(String name, String author, String album, int year, int duration, String genre) throws IOException;
    void editMusic(String musicToEdit, String name, String author, String album, int year, int duration, String genre) throws IOException;
    void removeMusic(String musicToRemove) throws IOException;
    void getMusic(String musicName) throws IOException;
    void addPlaylist(String name) throws IOException;
    void editPlaylist(String playlistToEdit, String name) throws IOException;
    void removePlaylist(String playlistToRemove) throws IOException;
    void addMusicToPlaylist(String musicName, String playlistName) throws IOException;
    void removeMusicFromPlaylist(String musicToRemove, String playlistName) throws IOException;
    void logout() throws IOException;

}
