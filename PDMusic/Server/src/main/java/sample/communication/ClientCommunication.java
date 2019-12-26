package sample.communication;

import org.json.JSONObject;
import sample.Communication;
import sample.communication.files.DownloadMusic;
import sample.communication.files.UploadMusic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientCommunication implements Runnable, Communication {

    public final String USERNAME_UNDEFINED = "undefined";

    private static int counter = 0;
    private final int id;

    ServerSocket musicTransferServerSocket;

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;

    private ClientNotificationsHandler clientNotificationsHandler;

    private ServerCommunication serverCommunication;

    private boolean isRunning = true;

    private JSONObject response;

    private String username = USERNAME_UNDEFINED;

    public ClientCommunication(Socket clientSocket, ClientNotificationsHandler clientNotificationsHandler, ServerCommunication serverCommunication) throws IOException {
        this.socket = clientSocket;
        pw = new PrintWriter(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        id = counter++;
        musicTransferServerSocket = new ServerSocket(0);
        this.clientNotificationsHandler = clientNotificationsHandler;
        this.serverCommunication = serverCommunication;
    }

    public int getId() {
        return id;
    }

    public void run() {
        String jsonRequest;

        try {
            while (isRunning) {
                try {
                    jsonRequest = br.readLine();

                    JSONObject request = new JSONObject(jsonRequest);
                    response = new JSONObject();
                    switch (request.getString(REQUEST)) {
                        case REQUEST_LOGIN:
                            String username = request.getString(USERNAME);
                            String password = request.getString(PASSWORD);

                            System.out.println("Login -> ID: " + id + " Username: " + username);

                            login(username, password);
                            break;
                        case REQUEST_REGISTER:
                            String name = request.getString(NAME);
                            username = request.getString(USERNAME);
                            password = request.getString(PASSWORD);

                            System.out.println("Register -> ID: " + id + " Username: " + username + " Name: " + name);

                            register(name, username, password);
                            break;
                        case REQUEST_ADD_MUSIC:
                            String musicName = request.getString(MUSIC_NAME);
                            String author = request.getString(AUTHOR);
                            String album = request.getString(ALBUM);
                            int year = request.getInt(YEAR);
                            int duration = request.getInt(DURATION);
                            String genre = request.getString(GENRE);

                            System.out.println("Add Music -> ID: " + id + " Username: " + this.username +
                                    " MusicName: " + musicName + " Author: " + author +
                                    " Album: " + album + " Year: " + year +
                                    " Duration: " + duration + " Genre: " + genre);

                            addMusic(musicName, author, album, year, duration, genre);
                            break;
                        case REQUEST_EDIT_MUSIC:
                            String musicToEdit = request.getString(MUSIC_TO_EDIT);
                            musicName = request.getString(MUSIC_NAME);
                            author = request.getString(AUTHOR);
                            album = request.getString(ALBUM);
                            year = request.getInt(YEAR);
                            duration = request.getInt(DURATION);
                            genre = request.getString(GENRE);

                            System.out.println("Edit Music -> ID: " + id + " Username: " + this.username +
                                    " MusicToEdit: " + musicToEdit +
                                    " MusicName: " + musicName + " Author: " + author +
                                    " Album: " + album + " Year: " + year +
                                    " Duration: " + duration + " Genre: " + genre);

                            editMusic(musicToEdit, musicName, author, album, year, duration, genre);
                            break;
                        case REQUEST_REMOVE_MUSIC:
                            musicName = request.getString(MUSIC_NAME);

                            System.out.println("Remove Music -> ID: " + id + " Username: " + this.username +
                                    " MusicToRemove: " + musicName);

                            removeMusic(musicName);
                            break;
                        case REQUEST_GET_MUSIC:
                            musicName = request.getString(MUSIC_NAME);

                            System.out.println("Get Music -> ID: " + id + " Requested Music: " + musicName);

                            getMusic(musicName);
                            break;
                        case REQUEST_ADD_PLAYLIST:
                            String playlistName = request.getString(PLAYLIST_NAME);

                            System.out.println("Add Playlist -> ID: " + id + " Username: " + this.username +
                                    " PlaylistName: " + playlistName);

                            addPlaylist(playlistName);
                            break;
                        case REQUEST_EDIT_PLAYLIST:
                            playlistName = request.getString(PLAYLIST_NAME);
                            String playlistToEdit = request.getString(PLAYLIST_TO_EDIT);

                            System.out.println("Edit Playlist -> ID: " + id + " Username: " + this.username +
                                    " PlaylistToEdit " + playlistToEdit + " PlaylistName: " + playlistName);

                            editPlaylist(playlistToEdit, playlistName);
                            break;
                        case REQUEST_REMOVE_PLAYLIST:
                            playlistName = request.getString(PLAYLIST_NAME);

                            System.out.println("Remove Playlist -> ID: " + id + " Username: " + this.username +
                                    " PlaylistToRemove: " + playlistName);

                            removePlaylist(playlistName);
                            break;
                        case REQUEST_ADD_MUSIC_TO_PLAYLIST:
                            String musicToAdd = request.getString(MUSIC_NAME);
                            String playlistForMusic = request.getString(PLAYLIST_NAME);

                            System.out.println("Add Music To Playlist -> ID: " + id +
                                    " Username: " + this.username +
                                    " MusicToAdd: " + musicToAdd +
                                    " PlaylistForMusic: " + playlistForMusic);

                            addMusicToPlaylist(musicToAdd, playlistForMusic);
                            break;
                        case REQUEST_REMOVE_MUSIC_FROM_PLAYLIST:
                            String musicToRemove = request.getString(MUSIC_NAME);
                            playlistName = request.getString(PLAYLIST_NAME);

                            System.out.println("Remove Music From Playlist -> ID: " + id +
                                    " Username: " + this.username +
                                    " MusicToRemove: " + musicToRemove +
                                    " Playlist: " + playlistName);

                            removeMusicFromPlaylist(musicToRemove, playlistName);
                            break;
                        case REQUEST_LOGOUT:
                            System.out.println("Logout -> ID: " + id + " Username: " + this.username);

                            logout();
                            break;
                        default:
                            break;
                    }
                } catch (NullPointerException e) {
                    isRunning = false;
                    //If server is running warn servers directory that client logged out
                    if (clientNotificationsHandler.isServerRunning()) {
                        logout();
                    }
                    System.out.println("Client Disconnected -> ID: " + id + " Username: " + username);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    synchronized void sendResponse(JSONObject response) {
        pw.println(response.toString());
        pw.flush();
    }

    @Override
    public void login(String username, String password) throws IOException {
        //TODO: Apply restrictions
        this.username = username;

        response.put(RESPONSE, REQUEST_LOGIN);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, LOGIN_SUCCESS);
        response.put(USERNAME, username);

        sendResponse(response);
        System.out.println(LOGIN_SUCCESS);
    }

    @Override
    public void register(String name, String username, String password) throws IOException {
        response.put(RESPONSE, REQUEST_REGISTER);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, username + " " + REGISTER_SUCCESS);
        //Send notification
        serverCommunication.registerNotification(new JSONObject(response.toString()));

        sendResponse(response);
        System.out.println(username + REGISTER_SUCCESS);
    }

    private void putMusicDetailsInResponse(String name, String author, String album, int year, int duration, String genre) {
        response.put(USERNAME, username);
        response.put(MUSIC_NAME, name);
        response.put(AUTHOR, author);
        response.put(ALBUM, album);
        response.put(YEAR, year);
        response.put(DURATION, duration);
        response.put(GENRE, genre);
    }

    @Override
    public void addMusic(String name, String author, String album, int year, int duration, String genre) {
        //Put music details
        putMusicDetailsInResponse(name, author, album, year, duration, genre);
        //Send notification
        serverCommunication.addMusicNotification(new JSONObject(response.toString()));
        clientNotificationsHandler.addMusicNotification(id, new JSONObject(response.toString()));
        //Put response data
        response.put(RESPONSE, REQUEST_ADD_MUSIC);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, ADD_MUSIC_SUCCESS);
        //TODO: Only send port if approved
        response.put(PORT, musicTransferServerSocket.getLocalPort());

        sendResponse(response);
        System.out.println(ADD_MUSIC_SUCCESS);

        downloadMusicFromClient(name);
    }

    @Override
    public void editMusic(String musicToEdit, String name, String author, String album, int year, int duration, String genre) {
        //Put music details
        response.put(MUSIC_TO_EDIT, musicToEdit);
        putMusicDetailsInResponse(name, author, album, year, duration, genre);
        //Send notification
        serverCommunication.editMusicNotification(new JSONObject(response.toString()));
        clientNotificationsHandler.editMusicNotification(id, new JSONObject(response.toString()));
        //Put response data
        response.put(RESPONSE, REQUEST_EDIT_MUSIC);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, EDIT_MUSIC_SUCCESS);
        //TODO: Only send port if approved
        response.put(PORT, musicTransferServerSocket.getLocalPort());

        sendResponse(response);
        System.out.println(EDIT_MUSIC_SUCCESS);

        downloadMusicFromClient(name);
    }

    @Override
    public void removeMusic(String musicToRemove) {
        //Put music details
        response.put(USERNAME, username);
        response.put(MUSIC_NAME, musicToRemove);
        //Send notification
        serverCommunication.removeMusicNotification(new JSONObject(response.toString()));
        clientNotificationsHandler.removeMusicNotification(id, new JSONObject(response.toString()));
        //Put response data
        response.put(RESPONSE, REQUEST_REMOVE_MUSIC);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, REMOVE_MUSIC_SUCCESS);

        sendResponse(response);
        System.out.println(REMOVE_MUSIC_SUCCESS);
    }

    @Override
    public void getMusic(String musicName) {
        response.put(MUSIC_NAME, musicName);
        response.put(PORT, musicTransferServerSocket.getLocalPort());

        //Put response data
        response.put(RESPONSE, REQUEST_GET_MUSIC);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, GET_MUSIC_SUCCESS);

        sendResponse(response);

        uploadMusicToClient(musicName);
    }

    private void uploadMusicToClient(String musicName) {
        try {
            UploadMusic uploadMusic = new UploadMusic(musicName, musicTransferServerSocket.accept());
            uploadMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadMusicFromClient(String musicName) {
        //TODO: Separate Exceptions!
        //TODO: Send to all servers
        try {
            DownloadMusic downloadMusic = new DownloadMusic(musicName, musicTransferServerSocket.accept());
            downloadMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPlaylist(String name) {
        //Put playlist details
        response.put(USERNAME, username);
        response.put(PLAYLIST_NAME, name);
        //Send notification
        serverCommunication.addPlaylistNotification(new JSONObject(response.toString()));
        clientNotificationsHandler.addPlaylistNotification(id, new JSONObject(response.toString()));
        //Put response data
        response.put(RESPONSE, REQUEST_ADD_PLAYLIST);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, ADD_PLAYLIST_SUCCESS);

        sendResponse(response);
        System.out.println(ADD_PLAYLIST_SUCCESS);
    }

    @Override
    public void editPlaylist(String playlistToEdit, String name) {
        //Put playlist details
        response.put(USERNAME, username);
        response.put(PLAYLIST_NAME, name);
        response.put(PLAYLIST_TO_EDIT, playlistToEdit);
        //Send notification
        serverCommunication.editPlaylistNotification(new JSONObject(response.toString()));
        clientNotificationsHandler.editPlaylistNotification(id, new JSONObject(response.toString()));
        //Put response data
        response.put(RESPONSE, REQUEST_EDIT_PLAYLIST);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, EDIT_PLAYLIST_SUCCESS);

        sendResponse(response);
        System.out.println(EDIT_PLAYLIST_SUCCESS);
    }

    @Override
    public void removePlaylist(String playlistToRemove) {
        //Put playlist details
        response.put(USERNAME, username);
        response.put(PLAYLIST_NAME, playlistToRemove);
        //Send notification
        serverCommunication.removePlaylistNotification(new JSONObject(response.toString()));
        clientNotificationsHandler.removePlaylistNotification(id, new JSONObject(response.toString()));
        //Put response data
        response.put(RESPONSE, REQUEST_REMOVE_PLAYLIST);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, REMOVE_PLAYLIST_SUCCESS);

        sendResponse(response);
        System.out.println(REMOVE_PLAYLIST_SUCCESS);
    }

    @Override
    public void addMusicToPlaylist(String musicName, String playlistName) {
        //Put music and playlist details
        response.put(USERNAME, username);
        response.put(MUSIC_NAME, musicName);
        response.put(PLAYLIST_NAME, playlistName);
        //Send notification
        serverCommunication.addMusicToPlaylistNotification(new JSONObject(response.toString()));
        clientNotificationsHandler.addMusicToPlaylistNotification(id, new JSONObject(response.toString()));
        //Put response data
        response.put(RESPONSE, REQUEST_ADD_MUSIC_TO_PLAYLIST);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, ADD_MUSIC_TO_PLAYLIST_SUCCESS);

        sendResponse(response);
        System.out.println(ADD_MUSIC_TO_PLAYLIST_SUCCESS);
    }

    @Override
    public void removeMusicFromPlaylist(String musicToRemove, String playlistName) {
        //Put music and playlist details
        response.put(USERNAME, username);
        response.put(MUSIC_NAME, musicToRemove);
        response.put(PLAYLIST_NAME, playlistName);
        //Send notification
        serverCommunication.removeMusicFromPlaylistNotification(new JSONObject(response.toString()));
        clientNotificationsHandler.removeMusicFromPlaylistNotification(id, new JSONObject(response.toString()));
        //Put response data
        response.put(RESPONSE, REQUEST_REMOVE_MUSIC_FROM_PLAYLIST);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, REMOVE_MUSIC_FROM_PLAYLIST_SUCCESS);

        sendResponse(response);
        System.out.println(REMOVE_MUSIC_FROM_PLAYLIST_SUCCESS);
    }

    @Override
    public void logout() {
        response.put(RESPONSE, REQUEST_LOGOUT);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, LOGOUT_SUCCESS);

        sendResponse(response);
        System.out.println(LOGOUT_SUCCESS);

        clientNotificationsHandler.clientLogout(id);

        shutdown();
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
}