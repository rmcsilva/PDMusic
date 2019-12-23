package sample.communication.interfaces;

import org.json.JSONObject;
import sample.JSONConstants;

public interface ServerNotifications extends JSONConstants {
    String NOTIFICATION_ID = "requestID";
    String NOTIFICATION_IP = "requestIP";
    String NOTIFICATION_TCP_PORT = "requestTcpPort";

    void sendDatabaseInformation(JSONObject database);
    void registerNotification(JSONObject register);
    void addMusicNotification(JSONObject music);
    void addPlaylistNotification(JSONObject playlist);
    void addMusicToPlaylistNotification(JSONObject musicToPlaylist);
    void serverShutdown();
}