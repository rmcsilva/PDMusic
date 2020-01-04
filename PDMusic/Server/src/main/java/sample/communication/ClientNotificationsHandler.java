package sample.communication;

import org.json.JSONArray;
import org.json.JSONObject;
import sample.ServerController;
import sample.communication.interfaces.ClientNotifications;
import sample.database.DatabaseAccess;
import sample.database.models.Music;
import sample.database.models.Playlist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientNotificationsHandler implements ClientNotifications {

    private ServerController serverController;

    private DatabaseAccess databaseAccess;

    private List<ClientCommunication> clientCommunications;

    public ClientNotificationsHandler(ServerController serverController, DatabaseAccess databaseAccess) {
        this.serverController = serverController;
        this.databaseAccess = databaseAccess;
        clientCommunications = new ArrayList<>();
    }

    public boolean isServerRunning() {
        return serverController.isServerRunning();
    }

    public void addClient(ClientCommunication newClient) {
        clientCommunications.add(newClient);
    }

    public void clientLogout(int id) {
        //Remove client from list
        for (int i = 0; i < clientCommunications.size(); i++) {
            if (clientCommunications.get(i).getId() == id) {
                clientCommunications.remove(i);
                break;
            }
        }
        serverController.clientLoggedOut();
    }

    public synchronized void sendNotificationToClients(int senderId, JSONObject notification) {
        for (ClientCommunication clientCommunication : clientCommunications) {
            //Only send notifications to other clients
            int id = clientCommunication.getId();
            if (id != senderId) {
                System.out.println("Notification Sent To -> ID: " + id +
                        " Type: " + notification.getString(NOTIFICATION) + "\n");
                clientCommunication.sendResponse(notification);
            }
        }
    }

    private JSONObject getMusicJSON(Music music) {
        JSONObject musicJSON = new JSONObject();
        musicJSON.put(USERNAME, music.getUsername());
        musicJSON.put(MUSIC_NAME, music.getName());
        musicJSON.put(AUTHOR, music.getAuthor());
        musicJSON.put(ALBUM, music.getAlbum());
        musicJSON.put(YEAR, music.getYear());
        musicJSON.put(DURATION, music.getDuration());
        musicJSON.put(GENRE, music.getGenre());
        return musicJSON;
    }

    private JSONObject getPlaylistJSON(Playlist playlist) {
        JSONObject playlistJSON = new JSONObject();
        playlistJSON.put(USERNAME, playlist.getUsername());
        playlistJSON.put(PLAYLIST_NAME, playlist.getName());
        return playlistJSON;
    }

    private JSONObject getMusicInPlaylistJSON(String playlistName, String musicName) {
        JSONObject musicInPlaylistJSON = new JSONObject();
        musicInPlaylistJSON.put(PLAYLIST_NAME, playlistName);
        musicInPlaylistJSON.put(MUSIC_NAME, musicName);
        return musicInPlaylistJSON;
    }

    public void putDatabaseInformationIntoJSON(JSONObject database) {
        try {
            List<Music> musics = databaseAccess.getMusics();
            JSONArray musicsJSON = new JSONArray();
            for(Music music : musics) {
                musicsJSON.put(getMusicJSON(music));
            }
            database.put(MUSICS_DATA, musicsJSON);

            List<Playlist> playlists = databaseAccess.getPlaylists();
            JSONArray playlistsJSON = new JSONArray();
            for(Playlist playlist : playlists) {
                playlistsJSON.put(getPlaylistJSON(playlist));
            }
            database.put(PLAYLISTS_DATA, playlistsJSON);

            Map<String, List<String>> musicsInPlaylist = databaseAccess.getMusicsInPlaylist();
            JSONArray musicsInPlaylistJSON = new JSONArray();
            for (Map.Entry<String, List<String>> entry : musicsInPlaylist.entrySet()) {
                for (String musicName : entry.getValue()) {
                    musicsInPlaylistJSON.put(getMusicInPlaylistJSON(entry.getKey(), musicName));
                }
            }
            database.put(MUSICS_IN_PLAYLIST_DATA, musicsInPlaylistJSON);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDatabaseInformation() {
        JSONObject database = new JSONObject();
        database.put(NOTIFICATION, DATABASE_INFORMATION);
        putDatabaseInformationIntoJSON(database);
        sendNotificationToClients(-1, database);
    }

    @Override
    public void addMusicNotification(int senderID, JSONObject music) {
        music.put(NOTIFICATION, REQUEST_ADD_MUSIC);
        sendNotificationToClients(senderID, music);
    }

    @Override
    public void editMusicNotification(int senderID, JSONObject music) {
        music.put(NOTIFICATION, REQUEST_EDIT_MUSIC);
        sendNotificationToClients(senderID, music);
    }

    @Override
    public void removeMusicNotification(int senderID, JSONObject music) {
        music.put(NOTIFICATION, REQUEST_REMOVE_MUSIC);
        sendNotificationToClients(senderID, music);
    }

    @Override
    public void addPlaylistNotification(int senderID, JSONObject playlist) {
        playlist.put(NOTIFICATION, REQUEST_ADD_PLAYLIST);
        sendNotificationToClients(senderID, playlist);
    }

    @Override
    public void editPlaylistNotification(int senderID, JSONObject playlist) {
        playlist.put(NOTIFICATION, REQUEST_EDIT_PLAYLIST);
        sendNotificationToClients(senderID, playlist);
    }

    @Override
    public void removePlaylistNotification(int senderID, JSONObject playlist) {
        playlist.put(NOTIFICATION, REQUEST_REMOVE_PLAYLIST);
        sendNotificationToClients(senderID, playlist);
    }

    @Override
    public void addMusicToPlaylistNotification(int senderID, JSONObject musicToPlaylist) {
        musicToPlaylist.put(NOTIFICATION, REQUEST_ADD_MUSIC_TO_PLAYLIST);
        sendNotificationToClients(senderID, musicToPlaylist);
    }

    @Override
    public void removeMusicFromPlaylistNotification(int senderID, JSONObject musicFromPlaylist) {
        musicFromPlaylist.put(NOTIFICATION, REQUEST_REMOVE_MUSIC_FROM_PLAYLIST);
        sendNotificationToClients(senderID, musicFromPlaylist);
    }

    @Override
    public void serverShutdown() {
        JSONObject serverShutdown = new JSONObject();
        serverShutdown.put(NOTIFICATION, SERVER_SHUTDOWN);
        //Sends the notification to all the clients, senderId starts at 0
        sendNotificationToClients(-1, serverShutdown);
    }
}
