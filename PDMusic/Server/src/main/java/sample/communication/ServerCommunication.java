package sample.communication;

import org.json.JSONException;
import org.json.JSONObject;
import sample.ServerController;
import sample.communication.interfaces.ServerNotifications;
import sample.communication.models.MulticastNotificationInformation;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static sample.ServersDirectoryInformation.*;

public class ServerCommunication extends Thread implements ServerNotifications {

    private static int requestID = 0;

    private final int multicastPort = 6001;
    private final String multicastIp = "230.30.30.30";
    private final InetAddress multicastGroup = InetAddress.getByName(multicastIp);

    private ServerController serverController;
    private ServerInformation serverInformation;

    List<MulticastResponseHandler> multicastResponseHandlers;

    private boolean isRunning = true;

    private boolean isPrimaryServer = false;

    private MulticastSocket multicastSocket;

    public ServerCommunication(ServerController serverController, String nic) throws IOException {
        this.serverController = serverController;
        serverInformation = serverController.getServerInformation();
        initializeServerCommunication(nic);
        multicastResponseHandlers = new ArrayList<>();
    }

    private void initializeServerCommunication(String nic) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        multicastSocket = new MulticastSocket(multicastPort);

        try {
            multicastSocket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getByName(nic)));
        } catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex) {
            multicastSocket.setNetworkInterface(NetworkInterface.getByName(nic)); //e.g., eth0, wlan0, en0
        }

        multicastSocket.joinGroup(multicastGroup);
    }

    public void setupAsPrimaryServer() {
        isPrimaryServer = true;
    }

    private synchronized int incrementRequestID() {
        return ++requestID;
    }

    protected HashSet<ServerInformation> getServers() {
        return serverController.getServers();
    }

    protected void removeMulticastResponseHandler(MulticastResponseHandler multicastResponseHandler) {
        multicastResponseHandlers.remove(multicastResponseHandler);
    }

    @Override
    public void run() {
        MulticastNotificationInformation notificationInformation;

        try {
            while (isRunning) {
                JSONObject notification = receiveMulticastNotification();

                try {
                    notificationInformation = getNotificationInformation(notification);
                } catch (JSONException e) {
                    System.out.println("Multicast Notification without server information!\n");
                    continue;
                }

                System.out.println("Multicast Notification ID -> " + notificationInformation.getNotificationId() +
                        " Server: " + notificationInformation.getServerInformation());

                if (notification.has(REQUEST)) {
                    handleRequest(notification, notificationInformation);
                } else if (notification.has(RESPONSE)) {
                    handleResponse(notification, notificationInformation);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject receiveMulticastNotification() throws IOException {
        byte[] bArray = new byte[datagramPacketSize];
        DatagramPacket datagramPacket = new DatagramPacket(bArray, bArray.length);
        multicastSocket.receive(datagramPacket);
        String jsonRequest = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        return new JSONObject(jsonRequest);
    }

    private void handleRequest(JSONObject request, MulticastNotificationInformation notificationInformation) {
        String username = request.getString(USERNAME);

        System.out.print("Multicast Request ");

        JSONObject requestAcknowledgment = new JSONObject();

        if (notificationInformation.getServerInformation().equals(serverInformation)) {
            System.out.println(" sent from this server, ignoring request!\n");
            return;
        }

        switch (request.getString(REQUEST)) {
            case REQUEST_REGISTER:
                String name = request.getString(NAME);
                String password = request.getString(PASSWORD);

                System.out.println("Register -> Username: " + username + " Name: " + name + "\n");

                requestAcknowledgment.put(RESPONSE, REQUEST_REGISTER);
                break;
            case REQUEST_ADD_MUSIC:
                String musicName = request.getString(MUSIC_NAME);
                String author = request.getString(AUTHOR);
                String album = request.getString(ALBUM);
                int year = request.getInt(YEAR);
                int duration = request.getInt(DURATION);
                String genre = request.getString(GENRE);

                System.out.println("Add Music -> Username: " + username +
                        " MusicName: " + musicName + " Author: " + author +
                        " Album: " + album + " Year: " + year +
                        " Duration: " + duration + " Genre: " + genre + "\n");

                requestAcknowledgment.put(RESPONSE, REQUEST_ADD_MUSIC);
                break;
            case REQUEST_EDIT_MUSIC:
                String musicToEdit = request.getString(MUSIC_TO_EDIT);
                musicName = request.getString(MUSIC_NAME);
                author = request.getString(AUTHOR);
                album = request.getString(ALBUM);
                year = request.getInt(YEAR);
                duration = request.getInt(DURATION);
                genre = request.getString(GENRE);

                System.out.println("Edit Music -> Username: " + username +
                        " MusicToEdit: " + musicToEdit +
                        " MusicName: " + musicName + " Author: " + author +
                        " Album: " + album + " Year: " + year +
                        " Duration: " + duration + " Genre: " + genre);

                requestAcknowledgment.put(RESPONSE, REQUEST_EDIT_MUSIC);

                break;
            case REQUEST_REMOVE_MUSIC:
                musicName = request.getString(MUSIC_NAME);

                System.out.println("Edit Music -> Username: " + username +
                        " MusicToRemove: " + musicName);

                requestAcknowledgment.put(RESPONSE, REQUEST_REMOVE_MUSIC);
                break;
            case REQUEST_ADD_PLAYLIST:
                String playlistName = request.getString(PLAYLIST_NAME);

                System.out.println("Add Playlist -> Username: " + username +
                        " PlaylistName: " + playlistName + "\n");

                requestAcknowledgment.put(RESPONSE, REQUEST_ADD_PLAYLIST);
                break;
            case REQUEST_EDIT_PLAYLIST:
                String playlistToEdit = request.getString(PLAYLIST_TO_EDIT);
                playlistName = request.getString(PLAYLIST_NAME);

                System.out.println("Edit Playlist -> Username: " + username +
                        " PlaylistToEdit: " + playlistToEdit +
                        " PlaylistName: " + playlistName + "\n");

                requestAcknowledgment.put(RESPONSE, REQUEST_ADD_PLAYLIST);

                break;
            case REQUEST_REMOVE_PLAYLIST:
                playlistName = request.getString(PLAYLIST_NAME);

                System.out.println("Remove Playlist -> Username: " + username +
                        " PlaylistToRemove: " + playlistName);

                requestAcknowledgment.put(RESPONSE, REQUEST_REMOVE_PLAYLIST);

                //TODO: Add to database warn clients
                break;
            case REQUEST_ADD_MUSIC_TO_PLAYLIST:
                String musicToAdd = request.getString(MUSIC_NAME);
                String playlistForMusic = request.getString(PLAYLIST_NAME);

                System.out.println("Add Music To Playlist -> Username: " + username +
                        " MusicToAdd: " + musicToAdd +
                        " PlaylistForMusic: " + playlistForMusic + "\n");

                requestAcknowledgment.put(RESPONSE, REQUEST_ADD_MUSIC_TO_PLAYLIST);
                break;
        }

        requestAcknowledgment.put(NOTIFICATION_ID, notificationInformation.getNotificationId());
        requestAcknowledgment.put(NOTIFICATION_IP, notificationInformation.getNotificationIp());
        requestAcknowledgment.put(NOTIFICATION_TCP_PORT, notificationInformation.getNotificationTcpPort());

        sendRequestAcknowledgment(requestAcknowledgment);
    }

    private void handleResponse(JSONObject notification, MulticastNotificationInformation notificationInformation) {
        if (!notificationInformation.getServerInformation().equals(serverInformation)) {
            System.out.println("Response is not for this server, ignoring! \n");
            return;
        }

        int responseID = notification.getInt(NOTIFICATION_ID);
        String responseIP = notification.getString(IP);
        int responseTcpPort = notification.getInt(TCP_PORT);

        ServerInformation responseServerInformation = new ServerInformation(responseIP, responseTcpPort);

        System.out.println("Got response from Server -> " + responseServerInformation +
                " NotificationID: " + responseID + "\n");

        for (MulticastResponseHandler responseHandler : multicastResponseHandlers) {
            if (responseHandler.getRequestID() == responseID) {
                responseHandler.addResponseFromServer(responseServerInformation);
                break;
            }
        }

    }

    private MulticastNotificationInformation getNotificationInformation(JSONObject notification) {
        int notificationId = notification.getInt(NOTIFICATION_ID);
        String notificationIp = notification.getString(NOTIFICATION_IP);
        int notificationTcpPort = notification.getInt(NOTIFICATION_TCP_PORT);

        return new MulticastNotificationInformation(notificationId, notificationIp, notificationTcpPort);
    }

    public synchronized void sendNotificationToAllServers(JSONObject notification) {
        byte[] bArray = notification.toString().getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(bArray, bArray.length, multicastGroup, multicastPort);
        try {
            multicastSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequestAcknowledgment(JSONObject requestAcknowledgment) {
        requestAcknowledgment.put(IP, serverInformation.getIp());
        requestAcknowledgment.put(TCP_PORT, serverInformation.getTcpPort());

        sendNotificationToAllServers(requestAcknowledgment);
    }

    private void createRequest(int requestID, JSONObject request) {
        //If there are no servers there is no need to send a request
        if (serverController.getServers().isEmpty()) return;

        request.put(NOTIFICATION_ID, requestID);
        request.put(NOTIFICATION_IP, serverInformation.getIp());
        request.put(NOTIFICATION_TCP_PORT, serverInformation.getTcpPort());

        MulticastResponseHandler multicastResponseHandler = new MulticastResponseHandler(this, requestID, request);
        multicastResponseHandlers.add(multicastResponseHandler);
        multicastResponseHandler.start();

        System.out.println("Multicast Request Created ID -> " + requestID + " Type: " + request.getString(REQUEST) + "\n");
    }


    @Override
    public void sendDatabaseInformation(JSONObject database) {
    }

    @Override
    public void registerNotification(JSONObject register) {
        register.put(REQUEST, REQUEST_REGISTER);
        createRequest(incrementRequestID(), register);
    }

    @Override
    public void addMusicNotification(JSONObject music) {
        music.put(REQUEST, REQUEST_ADD_MUSIC);
        createRequest(incrementRequestID(), music);
    }

    @Override
    public void editMusicNotification(JSONObject music) {
        music.put(REQUEST, REQUEST_EDIT_MUSIC);
        createRequest(incrementRequestID(), music);
    }

    @Override
    public void removeMusicNotification(JSONObject music) {
        music.put(REQUEST, REQUEST_REMOVE_MUSIC);
        createRequest(incrementRequestID(), music);
    }

    @Override
    public void addPlaylistNotification(JSONObject playlist) {
        playlist.put(REQUEST, REQUEST_ADD_PLAYLIST);
        createRequest(incrementRequestID(), playlist);
    }

    @Override
    public void editPlaylistNotification(JSONObject playlist) {
        playlist.put(REQUEST, REQUEST_EDIT_PLAYLIST);
        createRequest(incrementRequestID(), playlist);
    }

    @Override
    public void removePlaylistNotification(JSONObject playlist) {
        playlist.put(REQUEST, REQUEST_REMOVE_PLAYLIST);
        createRequest(incrementRequestID(), playlist);
    }

    @Override
    public void addMusicToPlaylistNotification(JSONObject musicToPlaylist) {
        musicToPlaylist.put(REQUEST, REQUEST_ADD_MUSIC_TO_PLAYLIST);
        createRequest(incrementRequestID(), musicToPlaylist);
    }

    @Override
    public void serverShutdown() {
        isRunning = false;
        try {
            multicastSocket.leaveGroup(multicastGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
        multicastSocket.close();
    }
}
