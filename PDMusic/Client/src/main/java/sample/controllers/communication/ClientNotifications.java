package sample.controllers.communication;

import sample.JSONConstants;

public interface ClientNotifications extends JSONConstants {
    void databaseInformation();
    void addMusicNotification(String username, String name, String author, String album, int year, int duration, String genre);
    void addPlaylistNotification(String username, String name);
    void addMusicToPlaylistNotification(String musicName, String playlistName);
    void serverShutdown();
}