package sampleRMI.communication.interfaces;

import org.json.JSONObject;

import java.rmi.Remote;

public interface MusicInterface extends Remote {
    void registerNotification(JSONObject register);

    void addMusicNotification(JSONObject music);

    void editMusicNotification(JSONObject music);

    void removeMusicNotification(JSONObject music);

    void addPlaylistNotification(JSONObject playlist);

    void editPlaylistNotification(JSONObject playlist);

    void removePlaylistNotification(JSONObject playlist);

    void addMusicToPlaylistNotification(JSONObject musicToPlaylist);

    void removeMusicFromPlaylistNotification(JSONObject musicFromPlaylist);
}
