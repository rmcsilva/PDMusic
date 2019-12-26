package sample.controllers.communication;

import org.json.JSONObject;
import sample.Communication;
import sample.controllers.MainController;
import sample.controllers.communication.Exceptions.NoServerAvailable;
import sample.exceptions.NoServersDirectory;
import sample.models.ClientInformation;
import sample.models.ServerInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;


public class CommunicationHandler extends Thread implements Communication {

    private ServersDirectoryCommunication serversDirectoryCommunication;
    private NotificationHandler notificationHandler;

    private ClientInformation clientInformation;

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;

    private JSONObject request;

    private boolean isRunning = true;

    public CommunicationHandler(String serversDirectoryIP) throws IOException, NoServerAvailable, NoServersDirectory {
        serversDirectoryCommunication = new ServersDirectoryCommunication(serversDirectoryIP);
        handleConnections();
        notificationHandler = new NotificationHandler(this);
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected InetAddress getSocketAddress() {
        return socket.getInetAddress();
    }

    public void handleConnections() throws IOException, NoServerAvailable, NoServersDirectory {
        ServerInformation serverInformation = serversDirectoryCommunication.connectToServersDirectory();
        connectToServer(serverInformation.getIp(), serverInformation.getTcpPort());
    }

    private void connectToServer(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(socket.getOutputStream());
        System.out.println("Connected to Server " + ip + ":" + port + "!");
    }

    private void sendRequest(JSONObject jsonRequest) {
        pw.println(jsonRequest.toString());
        pw.flush();
    }

    private JSONObject receiveResponse() throws IOException {
        return new JSONObject(br.readLine());
    }

    public void setNotificationHandlerMainController(MainController mainController) {
        notificationHandler.setMainController(mainController);
    }

    void shutdown() {
        isRunning = false;

        if (pw != null) {
            pw.close();
        }

        if (br != null) {
            try {
                br.close();
            } catch (IOException ignored) {
            }
        }

        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        JSONObject response;

        while (isRunning) {
            try {
                response = receiveResponse();
                System.out.println(response);

                if (response.has(RESPONSE)) {
                    //Got response to a request from the server
                    notificationHandler.handleServerResponse(response);
                } else if(response.has(NOTIFICATION)) {
                    //Got a notification from the server
                    notificationHandler.handleServerNotification(response);
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println("Server shutdown");
                try {
                    handleConnections();
                    login(clientInformation.getUsername(), clientInformation.getPassword());
                } catch (ConnectException ce) {
                    notificationHandler.serverShutdown();
                    System.out.println("Could not connect to server! Try again latter!");
                    ce.printStackTrace();
                } catch (NoServerAvailable | NoServersDirectory | IOException ex) {
                    notificationHandler.serverShutdown();
                    System.out.println(ex);
                    ex.printStackTrace();
                    return;
                }
            } catch (IOException e) {
                notificationHandler.serverShutdown();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void login(String username, String password) {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_LOGIN);
        request.put(USERNAME, username);
        request.put(PASSWORD, password);

        clientInformation = new ClientInformation(username, password);

        sendRequest(request);
    }

    @Override
    public void register(String name, String username, String password) throws IOException {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_REGISTER);
        request.put(NAME, name);
        request.put(USERNAME, username);
        request.put(PASSWORD, password);

        sendRequest(request);
    }

    private void putMusicInRequest(JSONObject request, String name, String author, String album, int year, int duration, String genre) {
        request.put(MUSIC_NAME, name);
        request.put(AUTHOR, author);
        request.put(ALBUM, album);
        request.put(YEAR, year);
        request.put(DURATION, duration);
        request.put(GENRE, genre);
    }

    @Override
    public void addMusic(String name, String author, String album, int year, int duration, String genre) {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_ADD_MUSIC);
        putMusicInRequest(request, name, author, album, year, duration, genre);

        sendRequest(request);
    }

    @Override
    public void editMusic(String musicToEdit, String name, String author, String album, int year, int duration, String genre) {
        request = new JSONObject();
        request.put(REQUEST, REQUEST_EDIT_MUSIC);
        request.put(MUSIC_TO_EDIT, musicToEdit);
        putMusicInRequest(request, name, author, album, year, duration, genre);

        sendRequest(request);
    }

    @Override
    public void removeMusic(String musicToRemove) {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_REMOVE_MUSIC);
        request.put(MUSIC_NAME, musicToRemove);

        sendRequest(request);
    }

    @Override
    public void getMusic(String musicName) {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_GET_MUSIC);
        request.put(MUSIC_NAME, musicName);

        sendRequest(request);
    }

    @Override
    public void addPlaylist(String name) {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_ADD_PLAYLIST);
        request.put(PLAYLIST_NAME, name);

        sendRequest(request);
    }

    @Override
    public void editPlaylist(String playlistToEdit, String name) {
        request = new JSONObject();
        request.put(REQUEST, REQUEST_EDIT_PLAYLIST);
        request.put(PLAYLIST_TO_EDIT, playlistToEdit);
        request.put(PLAYLIST_NAME, name);

        sendRequest(request);
    }

    @Override
    public void removePlaylist(String playlistToRemove) {
        request = new JSONObject();
        request.put(REQUEST, REQUEST_REMOVE_PLAYLIST);
        request.put(PLAYLIST_NAME, playlistToRemove);

        sendRequest(request);
    }

    @Override
    public void addMusicToPlaylist(String musicName, String playlistName) {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_ADD_MUSIC_TO_PLAYLIST);
        request.put(MUSIC_NAME, musicName);
        request.put(PLAYLIST_NAME, playlistName);

        sendRequest(request);
    }

    @Override
    public void logout() {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_LOGOUT);

        sendRequest(request);

        isRunning = false;
    }
}
