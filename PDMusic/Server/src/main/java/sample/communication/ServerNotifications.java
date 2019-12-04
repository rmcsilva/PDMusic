package sample.communication;

import org.json.JSONObject;
import sample.JSONConstants;

public interface ServerNotifications extends JSONConstants {
    void sendDatabaseInformation(ClientCommunication client);
    void addMusicNotification(int senderID, JSONObject music);
    void addPlaylistNotification(int senderID, JSONObject playlist);
    void addMusicToPlaylistNotification(int senderID, JSONObject musicToPlaylist);
    void serverShutdown();
}
