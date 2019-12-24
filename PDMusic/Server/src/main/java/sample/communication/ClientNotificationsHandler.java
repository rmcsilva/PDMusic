package sample.communication;

import org.json.JSONObject;
import sample.ServerController;
import sample.communication.interfaces.ClientNotifications;

import java.util.ArrayList;
import java.util.List;

public class ClientNotificationsHandler implements ClientNotifications {

    private ServerController serverController;

    private List<ClientCommunication> clientCommunications;

    public ClientNotificationsHandler(ServerController serverController) {
        this.serverController = serverController;
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

    @Override
    public void sendDatabaseInformation(ClientCommunication client) {
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
    public void addPlaylistNotification(int senderID, JSONObject playlist) {
        playlist.put(NOTIFICATION, REQUEST_ADD_PLAYLIST);
        sendNotificationToClients(senderID, playlist);
    }

    @Override
    public void addMusicToPlaylistNotification(int senderID, JSONObject musicToPlaylist) {
        musicToPlaylist.put(NOTIFICATION, REQUEST_ADD_MUSIC_TO_PLAYLIST);
        sendNotificationToClients(senderID, musicToPlaylist);
    }

    @Override
    public void serverShutdown() {
        JSONObject serverShutdown = new JSONObject();
        serverShutdown.put(NOTIFICATION, SERVER_SHUTDOWN);
        //Sends the notification to all the clients, senderId starts at 0
        sendNotificationToClients(-1, serverShutdown);
    }
}
