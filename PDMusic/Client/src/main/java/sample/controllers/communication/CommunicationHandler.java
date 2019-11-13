package sample.controllers.communication;

import org.json.JSONObject;
import sample.Communication;
import sample.exceptions.CountExceededException;
import sample.models.ServerInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


public class CommunicationHandler extends Thread implements Communication {

    private ServersDirectoryCommunication serversDirectoryCommunication;

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;

    private JSONObject request;

    private boolean isRunning = true;

    public CommunicationHandler(String serversDirectoryIP) throws CountExceededException, IOException {
        serversDirectoryCommunication = new ServersDirectoryCommunication(serversDirectoryIP);
        handleConnections();
//        notificationHandler = new NotificationHandler();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void handleConnections() throws CountExceededException, IOException {
        ServerInformation serverInformation = serversDirectoryCommunication.connectToServersDirectory();
        connectToServer(serverInformation.getIp(), serverInformation.getPort());
    }

    private void connectToServer(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(socket.getOutputStream());
        System.out.println("connected to server!");
    }

    private void sendRequest(JSONObject jsonRequest) {
        pw.println(jsonRequest.toString());
        pw.flush();
    }

    private JSONObject receiveResponse() throws IOException {
        return new JSONObject(br.readLine());
    }

    private void shutdown() {
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
            } catch (NullPointerException e) {
                shutdown();
                e.printStackTrace();
                System.out.println("Client logged out");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Server shutdown");
                try {
                    handleConnections();
                } catch (CountExceededException | IOException ex) {
                    shutdown();
                    e.printStackTrace();
                    System.out.println("Could not connect so servers directory!");
                    return;
                }
            }
        }
    }

    @Override
    public void login(String username, String password) {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_LOGIN);
        request.put(USERNAME, username);
        request.put(PASSWORD, password);

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

    @Override
    public void addMusic(String name, String author, String album, int year, int duration, String genre) {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_ADD_MUSIC);
        request.put(MUSIC_NAME, name);
        request.put(AUTHOR, author);
        request.put(ALBUM, album);
        request.put(YEAR, year);
        request.put(DURATION, duration);
        request.put(GENRE, genre);

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
    public void logout() {
        request = new JSONObject();
        request.put(REQUEST , REQUEST_LOGOUT);

        sendRequest(request);
        
        isRunning = false;
    }
}