package sample.communication;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sample.Communication;
import sample.communication.files.DownloadMusic;
import sample.communication.files.ServerFileManager;
import sample.communication.files.UploadMusic;
import sample.MessageDetails;
import sample.database.DatabaseAccess;
import sample.database.models.Music;
import sample.database.models.Playlist;
import sample.database.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientCommunication implements Runnable, Communication, MessageDetails {

    public final String USERNAME_UNDEFINED = "undefined";

    private static int counter = 0;
    private final int id;

    ServerSocket musicTransferServerSocket;

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;

    private ClientNotificationsHandler clientNotificationsHandler;

    private ServerCommunication serverCommunication;

    private DatabaseAccess databaseAccess;

    private List<DownloadMusic> musicsBeingDownloaded = new ArrayList<>();
    private List<UploadMusic> musicsBeingUploaded = new ArrayList<>();

    private boolean isRunning = true;

    private JSONObject response;

    private String username = USERNAME_UNDEFINED;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ClientCommunication(Socket clientSocket, ClientNotificationsHandler clientNotificationsHandler,
                               ServerCommunication serverCommunication, DatabaseAccess databaseAccess) throws IOException {
        this.socket = clientSocket;
        pw = new PrintWriter(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        id = counter++;
        musicTransferServerSocket = new ServerSocket(0);
        this.clientNotificationsHandler = clientNotificationsHandler;
        this.serverCommunication = serverCommunication;
        this.databaseAccess = databaseAccess;
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
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

                            downloadMusicFromClient(null, musicName, author, album, year, duration, genre);
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

                            downloadMusicFromClient(musicToEdit, musicName, author, album, year, duration, genre);
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

    private void requestApproved(String requestType, String details) {
        response.put(RESPONSE, requestType);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, details);
        sendResponse(response);
        System.out.println("Request Approved -> " + details);
    }

    private void requestDenied(String details) {
        response.put(RESPONSE, INVALID_REQUEST);
        response.put(STATUS, DENIED);
        response.put(DETAILS, details);
        sendResponse(response);
        System.out.println("Request Denied -> " + details);
    }

    @Override
    public void login(String username, String password) throws IOException {
        try {
            if (!databaseAccess.hasUsername(username)) {
                requestDenied(USERNAME_NOT_FOUND);
                return;
            }

            User user = databaseAccess.getUser(username);

            assert user != null;


            if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
                requestDenied(PASSWORD_MISMATCH);
                return;
            }

            this.username = username;
            response.put(USERNAME, username);
            putDatabaseInformationInRequest();
            requestApproved(REQUEST_LOGIN, LOGIN_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void putDatabaseInformationInRequest() {
        try {
            List<Music> musics = databaseAccess.getMusics();
            JSONArray musicsJSON = new JSONArray();
            for(Music music : musics) {
                musicsJSON.put(getMusicJSON(music));
            }
            response.put(MUSICS_DATA, musicsJSON);

            List<Playlist> playlists = databaseAccess.getPlaylists();
            JSONArray playlistsJSON = new JSONArray();
            for(Playlist playlist : playlists) {
                playlistsJSON.put(getPlaylistJSON(playlist));
            }
            response.put(PLAYLISTS_DATA, playlistsJSON);

            Map<String, List<String>> musicsInPlaylist = databaseAccess.getMusicsInPlaylist();
            JSONArray musicsInPlaylistJSON = new JSONArray();
            for (Map.Entry<String, List<String>> entry : musicsInPlaylist.entrySet()) {
                for (String musicName : entry.getValue()) {
                    musicsInPlaylistJSON.put(getMusicInPlaylistJSON(entry.getKey(), musicName));
                }
            }
            response.put(MUSICS_IN_PLAYLIST_DATA, musicsInPlaylistJSON);
        } catch (SQLException e) {
            e.printStackTrace();
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

    @Override
    public void register(String name, String username, String password) throws IOException {
        try {
            if (databaseAccess.hasUsername(username)) {
                requestDenied(USERNAME_ALREADY_TAKEN);
                return;
            }

            databaseAccess.addUser(new User(name, username, password));

            //Send notification
            JSONObject userData = new JSONObject();
            userData.put(USERNAME, username);
            userData.put(NAME, name);
            userData.put(PASSWORD, password);
            serverCommunication.registerNotification(userData);

            requestApproved(REQUEST_REGISTER, REGISTER_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean hasLoggedIn() {
        if (username.equals(USERNAME_UNDEFINED)) {
            requestDenied(USER_NOT_LOGGED_IN);
            return false;
        }
        return true;
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

    private String getRelativeMusicPath(String musicName) {
        return "/" + musicName + ".mp3";
    }

    private boolean isMusicBeingDownloaded(String musicName) {
        for(DownloadMusic downloadMusic : musicsBeingDownloaded) {
            if (downloadMusic.getMusicName().equals(musicName)) {
                return true;
            }
        }
        return false;
    }

    public void removeMusicFromBeingDownloaded(DownloadMusic music) {
        musicsBeingDownloaded.remove(music);
    }

    private void downloadMusicFromClient(String musicToEdit, String name, String author, String album, int year, int duration, String genre) {
        if (!hasLoggedIn()) {
            return;
        }

        response.put(MUSIC_NAME, name);

        try {
            if (databaseAccess.hasMusic(name)) {
                requestDenied(MUSIC_ALREADY_EXISTS);
                return;
            }

            if (isMusicBeingDownloaded(name)) {
                requestDenied(MUSIC_ALREADY_BEING_DOWNLOADED);
                return;
            }

            Music music = new Music(name, author, album, year, duration, genre);
            try {
                DownloadMusic downloadMusic;

                //Put response data
                response.put(PORT, musicTransferServerSocket.getLocalPort());
                requestApproved(REQUEST_UPLOAD_MUSIC, UPLOAD_MUSIC_SUCCESS);

                Socket socket = musicTransferServerSocket.accept();

                if (musicToEdit == null) {
                    //Add music request
                    downloadMusic = new DownloadMusic(this, music, socket);
                } else {
                    //Edit music request
                    downloadMusic = new DownloadMusic(this, music, musicToEdit, socket);
                }

                musicsBeingDownloaded.add(downloadMusic);
                downloadMusic.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMusic(String name, String author, String album, int year, int duration, String genre) {
        if (!hasLoggedIn()) {
            return;
        }

        try {
            if (databaseAccess.hasMusic(name)) {
                response.put(MUSIC_NAME, name);
                requestDenied(MUSIC_ALREADY_EXISTS);
                return;
            }

            databaseAccess.addMusic(new Music(databaseAccess.getUserIDFromUsername(username), name, author, album, year, duration, genre, getRelativeMusicPath(name)));

            //Put music details
            putMusicDetailsInResponse(name, author, album, year, duration, genre);
            //Send notification
            //TODO: Send music file to all servers
            serverCommunication.addMusicNotification(new JSONObject(response.toString()));
            clientNotificationsHandler.addMusicNotification(id, new JSONObject(response.toString()));

            requestApproved(REQUEST_ADD_MUSIC, ADD_MUSIC_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean canChangeMusic(String musicName, String errorType) {
        if (!hasLoggedIn()) {
            return false;
        }

        try {
            if (!databaseAccess.hasMusic(musicName)) {
                requestDenied(MUSIC_NOT_EXISTS);
                return false;
            }

            if(!username.equals(databaseAccess.getMusicOwner(musicName))) {
                requestDenied(errorType);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void editMusic(String musicToEdit, String name, String author, String album, int year, int duration, String genre) {
        try {
            if (!canChangeMusic(musicToEdit, EDIT_DIFFERENT_MUSIC_OWNER)) {
                return;
            }

            if (databaseAccess.hasMusic(name)) {
                response.put(MUSIC_NAME, name);
                requestDenied(MUSIC_ALREADY_EXISTS);
                return;
            }

            databaseAccess.editMusic(musicToEdit, new Music(databaseAccess.getUserIDFromMusicName(musicToEdit),name, author, album, year, duration, genre, getRelativeMusicPath(name)));

            //Put music details
            response.put(MUSIC_TO_EDIT, musicToEdit);
            putMusicDetailsInResponse(name, author, album, year, duration, genre);
            //Send notification
            //TODO: Send music file to all servers
            serverCommunication.editMusicNotification(new JSONObject(response.toString()));
            clientNotificationsHandler.editMusicNotification(id, new JSONObject(response.toString()));
            //Put response data
            requestApproved(REQUEST_EDIT_MUSIC, EDIT_MUSIC_SUCCESS);

            //Delete old file
            if (!musicToEdit.equals(name)) {
                try {
                    Files.delete(Paths.get(ServerFileManager.getMusicPath(musicToEdit)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMusic(String musicToRemove) {
        try {
            if (!canChangeMusic(musicToRemove, REMOVE_DIFFERENT_MUSIC_OWNER)) {
                return;
            }
            databaseAccess.removeMusic(musicToRemove);
            //Put music details
            response.put(USERNAME, username);
            response.put(MUSIC_NAME, musicToRemove);
            //Send notification
            serverCommunication.removeMusicNotification(new JSONObject(response.toString()));
            clientNotificationsHandler.removeMusicNotification(id, new JSONObject(response.toString()));
            //Put response data
            requestApproved(REQUEST_REMOVE_MUSIC, REMOVE_MUSIC_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isMusicBeingUploaded(String musicName) {
        for (UploadMusic uploadMusic : musicsBeingUploaded) {
            if (uploadMusic.getMusicName().equals(musicName)) {
                return true;
            }
        }
        return false;
    }

    public void removeMusicFromBeingUploaded(UploadMusic uploadMusic) {
        musicsBeingUploaded.remove(uploadMusic);
    }

    private void uploadMusicToClient(String musicName) {
        try {
            UploadMusic uploadMusic = new UploadMusic(this, musicName, musicTransferServerSocket.accept());
            uploadMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getMusic(String musicName) {
        if (isMusicBeingUploaded(musicName)) {
            requestDenied(MUSIC_ALREADY_BEING_UPLOADED);
            return;
        }

        response.put(MUSIC_NAME, musicName);
        response.put(PORT, musicTransferServerSocket.getLocalPort());

        //Put response data
        response.put(RESPONSE, REQUEST_GET_MUSIC);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, GET_MUSIC_SUCCESS);

        sendResponse(response);

        uploadMusicToClient(musicName);
    }

    @Override
    public void addPlaylist(String name) {
        if (!hasLoggedIn()) {
            return;
        }

        try {
            if (databaseAccess.hasPlaylist(name)) {
                requestDenied(PLAYLIST_ALREADY_EXISTS);
                return;
            }
            databaseAccess.addPlaylist(new Playlist(databaseAccess.getUserIDFromUsername(username), name));
            //Put playlist details
            response.put(USERNAME, username);
            response.put(PLAYLIST_NAME, name);
            //Send notification
            serverCommunication.addPlaylistNotification(new JSONObject(response.toString()));
            clientNotificationsHandler.addPlaylistNotification(id, new JSONObject(response.toString()));
            //Put response data
            requestApproved(REQUEST_ADD_PLAYLIST, ADD_PLAYLIST_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean canChangePlaylist(String playlistName, String errorType) {
        if (!hasLoggedIn()) {
            return false;
        }

        try {
            if (!databaseAccess.hasPlaylist(playlistName)) {
                requestDenied(PLAYLIST_NOT_EXISTS);
                return false;
            }

            if (!username.equals(databaseAccess.getPlaylistOwner(playlistName))) {
                requestDenied(errorType);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void editPlaylist(String playlistToEdit, String name) {
        try {
            if(!canChangePlaylist(playlistToEdit, EDIT_DIFFERENT_PLAYLIST_OWNER)) {
                return;
            }
            databaseAccess.editPlaylist(playlistToEdit, name);
            //Put playlist details
            response.put(USERNAME, username);
            response.put(PLAYLIST_NAME, name);
            response.put(PLAYLIST_TO_EDIT, playlistToEdit);
            //Send notification
            serverCommunication.editPlaylistNotification(new JSONObject(response.toString()));
            clientNotificationsHandler.editPlaylistNotification(id, new JSONObject(response.toString()));
            //Put response data
            requestApproved(REQUEST_EDIT_PLAYLIST, EDIT_PLAYLIST_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePlaylist(String playlistToRemove) {
        try {
            if(!canChangePlaylist(playlistToRemove, REMOVE_DIFFERENT_PLAYLIST_OWNER)) {
                return;
            }
            databaseAccess.removePlaylist(playlistToRemove);
            //Put playlist details
            response.put(USERNAME, username);
            response.put(PLAYLIST_NAME, playlistToRemove);
            //Send notification
            serverCommunication.removePlaylistNotification(new JSONObject(response.toString()));
            clientNotificationsHandler.removePlaylistNotification(id, new JSONObject(response.toString()));
            //Put response data
            requestApproved(REQUEST_REMOVE_PLAYLIST, REMOVE_PLAYLIST_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMusicToPlaylist(String musicName, String playlistName) {
        try {
            if (!databaseAccess.hasMusic(musicName)) {
                requestDenied(MUSIC_NOT_EXISTS);
                return;
            }

            if(!canChangePlaylist(playlistName, ADD_TO_PLAYLIST_DIFFERENT_OWNER)) {
                return;
            }

            databaseAccess.addMusicToPlaylist(databaseAccess.getPlaylistID(playlistName), databaseAccess.getMusicID(musicName));

            //Put music and playlist details
            response.put(USERNAME, username);
            response.put(MUSIC_NAME, musicName);
            response.put(PLAYLIST_NAME, playlistName);
            //Send notification
            serverCommunication.addMusicToPlaylistNotification(new JSONObject(response.toString()));
            clientNotificationsHandler.addMusicToPlaylistNotification(id, new JSONObject(response.toString()));
            //Put response data
            requestApproved(REQUEST_ADD_MUSIC_TO_PLAYLIST, ADD_MUSIC_TO_PLAYLIST_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMusicFromPlaylist(String musicToRemove, String playlistName) {
        try {
            if (!databaseAccess.hasMusic(musicToRemove)) {
                requestDenied(MUSIC_NOT_EXISTS);
                return;
            }

            if(!canChangePlaylist(playlistName, REMOVE_FROM_PLAYLIST_DIFFERENT_OWNER)) {
                return;
            }

            databaseAccess.removeMusicFromPlaylist(databaseAccess.getPlaylistID(playlistName), databaseAccess.getMusicID(musicToRemove));

            //Put music and playlist details
            response.put(USERNAME, username);
            response.put(MUSIC_NAME, musicToRemove);
            response.put(PLAYLIST_NAME, playlistName);
            //Send notification
            serverCommunication.removeMusicFromPlaylistNotification(new JSONObject(response.toString()));
            clientNotificationsHandler.removeMusicFromPlaylistNotification(id, new JSONObject(response.toString()));
            //Put response data
            requestApproved(REQUEST_REMOVE_MUSIC_FROM_PLAYLIST, REMOVE_MUSIC_FROM_PLAYLIST_SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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