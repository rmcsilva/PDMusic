package sample;

public interface Notifications extends JSONConstants {
    void databaseInformation();
    void addMusicNotification(String username, String name, String author, String album, int year, int duration, String genre);
    void addPlaylistNotification(String username, String name);
    void serverShutdown();
}
