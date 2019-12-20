package sample.communication.interfaces;

import org.json.JSONObject;
import sample.JSONConstants;
import sample.communication.ClientCommunication;

public interface ClientNotifications extends JSONConstants {
    void sendDatabaseInformation(ClientCommunication client);
    void addMusicNotification(int senderID, JSONObject music);
    void addPlaylistNotification(int senderID, JSONObject playlist);
    void addMusicToPlaylistNotification(int senderID, JSONObject musicToPlaylist);
    void serverShutdown();
}
