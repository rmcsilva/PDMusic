package sample.controllers.communication;

import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.controllers.MainController;
import sample.controllers.ScreenController;
import sample.controllers.communication.files.ClientFileManager;
import sample.controllers.communication.files.DownloadMusic;
import sample.controllers.communication.files.UploadMusic;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import static sample.MessageDetails.MUSIC_ALREADY_EXISTS;

public class NotificationHandler implements ClientNotifications {

    private CommunicationHandler communicationHandler;

    private ScreenController screenController;
    private MainController mainController;

    private boolean hasFinishedSetup = false;

    private String dialogHeading = "";
    private String dialogBody = "";

    private final String requestDenied = "Request Denied";

    public NotificationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
        screenController = ScreenController.getInstance();
    }

    private void showDialog(String heading, String body) {
        System.out.println(dialogBody);
        if (hasFinishedSetup || heading.equals(requestDenied)) {
            Platform.runLater(() -> screenController.showDialog(heading, body));
        }
        dialogBody = "";
    }

    public void handleServerResponse(JSONObject response) {

        String responseStatus = response.getString(STATUS);
        String messageDetails = response.getString(DETAILS);

        if (!isResponseApproved(responseStatus)) {
            if (messageDetails.equals(MUSIC_ALREADY_EXISTS)) {
                try {
                    Files.delete(Paths.get(ClientFileManager.getMusicPath(response.getString(MUSIC_NAME))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            dialogHeading = requestDenied;
            dialogBody = dialogBody.concat(messageDetails);
            showDialog(dialogHeading, dialogBody);
            return;
        }

        dialogHeading = "Request Accepted";

        switch (response.getString(RESPONSE)) {
            case REQUEST_LOGIN:
                System.out.println("Login Response -> Status: " + responseStatus);

                mainController.setUsername(response.getString(USERNAME));
                databaseInformation(response);
                screenController.activate(ScreenController.Screen.MAIN);
                hasFinishedSetup = true;

                break;
            case REQUEST_REGISTER:
                System.out.println("Register Response -> Status: " + responseStatus);

                screenController.activate(ScreenController.Screen.LOGIN);

                break;
            case REQUEST_UPLOAD_MUSIC:
                System.out.println("Upload Music Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                uploadMusicToServer(response);
                break;
            case REQUEST_ADD_MUSIC:
                System.out.println("Add Music Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parseMusicFromJSON(response, true);
                break;
            case REQUEST_EDIT_MUSIC:
                System.out.println("Edit Music Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parseMusicFromJSON(response, false);
                break;
            case REQUEST_REMOVE_MUSIC:
                System.out.println("Remove Music Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parseMusicToRemoveFromJSON(response);

                break;
            case REQUEST_GET_MUSIC:
                System.out.println("Get Music Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parseMusicToDownloadFromJSON(response);

                break;
            case REQUEST_ADD_PLAYLIST:
                System.out.println("Add Playlist Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parsePlaylistFromJSON(response, true);

                break;
            case REQUEST_EDIT_PLAYLIST:
                System.out.println("Edit Playlist Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parsePlaylistFromJSON(response, false);

                break;
            case REQUEST_REMOVE_PLAYLIST:
                System.out.println("Remove Playlist Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parsePlaylistToRemoveFromJSON(response);

                break;
            case REQUEST_ADD_MUSIC_TO_PLAYLIST:
                System.out.println("Add Music To Playlist Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parseMusicToAddToPlaylistFromJSON(response);

                break;
            case REQUEST_REMOVE_MUSIC_FROM_PLAYLIST:
                System.out.println("Remove Music From Playlist Response -> Status: " + responseStatus);

                dialogBody = dialogBody.concat(messageDetails);
                parseMusicToRemoveFromPlaylistFromJSON(response);

                break;
            case REQUEST_LOGOUT:
                System.out.println("Logout Response -> Status: " + responseStatus);

                screenController.activate(ScreenController.Screen.LOGIN);
                communicationHandler.shutdown();

                break;
            default:
                break;
        }
    }

    public void handleServerNotification(JSONObject notification) {

        dialogHeading = "Notification";

        switch (notification.getString(NOTIFICATION)) {
            case DATABASE_INFORMATION:
                System.out.println("Database Information Notification");
                databaseInformation(notification);
                break;
            case REQUEST_ADD_MUSIC:
                System.out.println("Add Music Notification");
                parseMusicFromJSON(notification, true);
                break;
            case REQUEST_EDIT_MUSIC:
                System.out.println("Edit music notification");
                parseMusicFromJSON(notification, false);
                break;
            case REQUEST_REMOVE_MUSIC:
                System.out.println("Remove music notification");
                parseMusicToRemoveFromJSON(notification);
                break;
            case REQUEST_ADD_PLAYLIST:
                System.out.println("Add Playlist Notification");
                parsePlaylistFromJSON(notification, true);
                break;
            case REQUEST_EDIT_PLAYLIST:
                System.out.println("Edit Playlist Notification");
                parsePlaylistFromJSON(notification, false);
                break;
            case REQUEST_REMOVE_PLAYLIST:
                System.out.println("Remove Playlist Notification");
                parsePlaylistToRemoveFromJSON(notification);
                break;
            case REQUEST_ADD_MUSIC_TO_PLAYLIST:
                System.out.println("Add Music To Playlist Notification ");
                parseMusicToAddToPlaylistFromJSON(notification);
                break;
            case REQUEST_REMOVE_MUSIC_FROM_PLAYLIST:
                System.out.println("Remove Music From Playlist Notification ");
                parseMusicToRemoveFromPlaylistFromJSON(notification);
                break;
            case SERVER_SHUTDOWN:
                System.out.println("Server Shutdown Notification");
                serverShutdown();
                break;
            default:
                break;
        }
    }

    private void parseMusicToRemoveFromJSON(JSONObject music) {
        String username = music.getString(USERNAME);
        String musicToRemove = music.getString(MUSIC_NAME);

        dialogBody = dialogBody.concat("Remove Music -> Username: " + username +
                " MusicToRemove: " + musicToRemove);

        removeMusicNotification(username, musicToRemove);
    }

    private void parseMusicFromJSON(JSONObject music, boolean addMusic) {
        String username = music.getString(USERNAME);
        String musicName = music.getString(MUSIC_NAME);
        String author = music.getString(AUTHOR);
        String album = music.getString(ALBUM);
        int year = music.getInt(YEAR);
        int duration = music.getInt(DURATION);
        String genre = music.getString(GENRE);

        //Check if needs to add or edit music
        if (addMusic) {
            dialogBody = dialogBody.concat("Add Music -> Username: " + username +
                    " MusicName: " + musicName + " Author: " + author + " Album: " + album +
                    " Year: " + year + " Duration: " + duration + " Genre: " + genre);

            addMusicNotification(username, musicName, author, album, year, duration, genre);
        } else {
            String musicToEdit = music.getString(MUSIC_TO_EDIT);

            dialogBody = dialogBody.concat("Edit Music -> Username: " + username + " MusicToEdit " + musicToEdit +
                    " MusicName: " + musicName + " Author: " + author + " Album: " + album +
                    " Year: " + year + " Duration: " + duration + " Genre: " + genre);

            editMusicNotification(username, musicToEdit, musicName, author, album, year, duration, genre);

            if (!musicToEdit.equals(musicName)) {
                try {
                    Files.delete(Paths.get(ClientFileManager.getMusicPath(musicToEdit)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseMusicToDownloadFromJSON(JSONObject musicToDownload) {
        String musicName = musicToDownload.getString(MUSIC_NAME);
        int port = musicToDownload.getInt(PORT);

        dialogBody = dialogBody.concat("Download Music -> " + musicName + " Port: " + port);

        downloadMusic(musicName, port);

    }

    private void parsePlaylistFromJSON(JSONObject playlist, boolean addPlaylist) {
        String username = playlist.getString(USERNAME);
        String playlistName = playlist.getString(PLAYLIST_NAME);

        //Check if needs to add or edit playlist
        if (addPlaylist) {
            dialogBody = dialogBody.concat("Add Playlist -> Username: " + username +
                    " PlaylistName: " + playlistName);

            addPlaylistNotification(username, playlistName);
        } else {
            String playlistToEdit = playlist.getString(PLAYLIST_TO_EDIT);

            dialogBody = dialogBody.concat("Edit Playlist -> Username: " + username +
                    " PlaylistToEdit: " + playlistToEdit +
                    " PlaylistName: " + playlistName);

            editPlaylistNotification(username, playlistToEdit, playlistName);
        }
    }

    private void parsePlaylistToRemoveFromJSON(JSONObject playlist) {
        String username = playlist.getString(USERNAME);
        String playlistToRemove = playlist.getString(PLAYLIST_NAME);

        dialogBody = dialogBody.concat("Remove Playlist -> Username: " + username +
                " PlaylistToRemove: " + playlistToRemove);

        removePlaylistNotification(username, playlistToRemove);
    }

    private void parseMusicToAddToPlaylistFromJSON(JSONObject musicToAddToPlaylist) {
        String username = null;

        if (musicToAddToPlaylist.has(USERNAME)) {
            username = musicToAddToPlaylist.getString(USERNAME);
        }
        String musicName = musicToAddToPlaylist.getString(MUSIC_NAME);
        String playlistName = musicToAddToPlaylist.getString(PLAYLIST_NAME);

        dialogBody = dialogBody.concat("Add Music To Playlist ->");

        if (username != null) {
            dialogBody = dialogBody.concat(" Username: " + username);
        }

        dialogBody = dialogBody.concat(" MusicName: " + musicName + " PlaylistName: " + playlistName);

        addMusicToPlaylistNotification(musicName, playlistName);
    }

    private void parseMusicToRemoveFromPlaylistFromJSON(JSONObject musicToAddToPlaylist) {
        String username = musicToAddToPlaylist.getString(USERNAME);
        String musicToRemove = musicToAddToPlaylist.getString(MUSIC_NAME);
        String playlistName = musicToAddToPlaylist.getString(PLAYLIST_NAME);

        dialogBody = dialogBody.concat("Remove Music From Playlist -> Username: " + username +
                " MusicToRemove: " + musicToRemove +
                " PlaylistName: " + playlistName);

        removeMusicFromPlaylistNotification(musicToRemove, playlistName);
    }

    private boolean isResponseApproved(String responseStatus) {
        return responseStatus.equals(APPROVED);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void databaseInformation(JSONObject data) {
        mainController.clearData();

        JSONArray musicsJSON = data.getJSONArray(MUSICS_DATA);
        for (int i = 0; i < musicsJSON.length(); i++) {
            parseMusicFromJSON(musicsJSON.getJSONObject(i), true);
        }

        JSONArray playlistsJSON = data.getJSONArray(PLAYLISTS_DATA);
        for (int i = 0; i < playlistsJSON.length(); i++) {
            parsePlaylistFromJSON(playlistsJSON.getJSONObject(i), true);
        }

        JSONArray musicsInPlaylistJSON = data.getJSONArray(MUSICS_IN_PLAYLIST_DATA);
        for (int i = 0; i < musicsInPlaylistJSON.length(); i++) {
            parseMusicToAddToPlaylistFromJSON(musicsInPlaylistJSON.getJSONObject(i));
        }
    }

    @Override
    public void addMusicNotification(String username, String name, String author, String album, int year, int duration, String genre) {
        mainController.addMusic(username, name, author, album, year, duration, genre);
        showDialog(dialogHeading, dialogBody);
    }

    @Override
    public void editMusicNotification(String username, String musicToEdit, String name, String author, String album, int year, int duration, String genre) {
        mainController.editMusic(username, musicToEdit, name, author, album, year, duration, genre);
        showDialog(dialogHeading, dialogBody);
    }

    @Override
    public void removeMusicNotification(String username, String musicToRemove) {
        mainController.removeMusic(musicToRemove);
        showDialog(dialogHeading, dialogBody);
    }

    @Override
    public void downloadMusic(String musicName, int port) {
        try {
            //TODO: Add to queue of requests
            DownloadMusic downloadMusic = new DownloadMusic(musicName, new Socket(communicationHandler.getSocketAddress(), port));
            downloadMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadMusicToServer(JSONObject response) {
        //Upload music to server
        int port = response.getInt(PORT);
        uploadMusic(response.getString(MUSIC_NAME), port);
    }

    @Override
    public void uploadMusic(String musicName, int port) {
        try {
            //TODO: Add to queue of requests
            UploadMusic uploadMusic = new UploadMusic(musicName, new Socket(communicationHandler.getSocketAddress(), port));
            uploadMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPlaylistNotification(String username, String name) {
        mainController.addPlaylist(username, name);
        showDialog(dialogHeading, dialogBody);
    }

    @Override
    public void editPlaylistNotification(String username, String playlistToEdit, String name) {
        mainController.editPlaylist(username, playlistToEdit, name);
        showDialog(dialogHeading, dialogBody);
    }

    @Override
    public void removePlaylistNotification(String username, String playlistToRemove) {
        mainController.removePlaylist(playlistToRemove);
        showDialog(dialogHeading, dialogBody);
    }

    @Override
    public void addMusicToPlaylistNotification(String musicName, String playlistName) {
        try {
            mainController.addMusicToPlaylist(playlistName, musicName);
            showDialog(dialogHeading, dialogBody);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMusicFromPlaylistNotification(String musicToRemove, String playlistName) {
        try {
            mainController.removeMusicFromPlaylist(playlistName, musicToRemove);
            showDialog(dialogHeading, dialogBody);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverShutdown() {
        communicationHandler.shutdown();
        screenController.activate(ScreenController.Screen.LOGIN);
        dialogBody = "The server you were connected shutdown!\n Try logging in again to connect to other server!";
        showDialog(dialogHeading, dialogBody);
    }
}
