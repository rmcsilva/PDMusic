package sample.controllers.communication;

import org.json.JSONObject;
import sample.controllers.MainController;
import sample.controllers.ScreenController;
import sample.controllers.communication.files.DownloadMusic;
import sample.controllers.communication.files.UploadMusic;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

public class NotificationHandler implements ClientNotifications {

    private CommunicationHandler communicationHandler;

    private ScreenController screenController;
    private MainController mainController;

    public NotificationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
        screenController = ScreenController.getInstance();
    }

    public void handleServerResponse(JSONObject response) {

        String responseStatus = response.getString(STATUS);
        boolean approved = isResponseApproved(responseStatus);

        switch (response.getString(RESPONSE)) {
            case REQUEST_LOGIN:
                System.out.println("Login Response -> Status: " + responseStatus);

                if (approved) {
                    mainController.setUsername(response.getString(USERNAME));
                    screenController.activate(ScreenController.Screen.MAIN);
                    //TODO: Show details
                }
                //TODO: Show error

                break;
            case REQUEST_REGISTER:
                System.out.println("Register Response -> Status: " + responseStatus);

                if (approved) {
                    screenController.activate(ScreenController.Screen.LOGIN);
                }
                //TODO: Show error

                break;
            case REQUEST_ADD_MUSIC:
                System.out.println("Add Music Response -> Status: " + responseStatus);

                if (approved) {
                    parseMusicFromJSON(response, true);

                    uploadMusicToServer(response);
                }
                //TODO: Show error
                break;
            case REQUEST_EDIT_MUSIC:
                System.out.println("Edit Music Response -> Status: " + responseStatus);

                if (approved) {
                    parseMusicFromJSON(response, false);

                    uploadMusicToServer(response);
                }
                //TODO: Show error
                break;
            case REQUEST_GET_MUSIC:
                System.out.println("Get Music Response -> Status: " + responseStatus);

                if (approved) {
                    parseMusicToDownloadFromJSON(response);
                }
                //TODO: Show error

                break;
            case REQUEST_ADD_PLAYLIST:
                System.out.println("Add Playlist Response -> Status: " + responseStatus);

                if (approved) {
                    parsePlaylistFromJSON(response);
                }
                //TODO: Show error

                break;
            case REQUEST_ADD_MUSIC_TO_PLAYLIST:
                System.out.println("Add Music To Playlist Response -> Status: " + responseStatus);

                if (approved) {
                    parseMusicToAddToPlaylistFromJSON(response);
                }
                //TODO: Show error

                break;
            case REQUEST_LOGOUT:
                System.out.println("Logout Response -> Status: " + responseStatus);

                if (approved) {
                    screenController.activate(ScreenController.Screen.LOGIN);
                    communicationHandler.shutdown();
                }
                //TODO: Show error

                break;
            default:
                break;
        }
    }

    public void handleServerNotification(JSONObject notification) {
        switch (notification.getString(NOTIFICATION)) {
            case REQUEST_ADD_MUSIC:
                System.out.println("Add Music Notification");
                parseMusicFromJSON(notification, true);
                break;
            case REQUEST_EDIT_MUSIC:
                System.out.println("Edit music notification");
                parseMusicFromJSON(notification, false);
                break;
            case REQUEST_ADD_PLAYLIST:
                System.out.println("Add Playlist Notification");
                parsePlaylistFromJSON(notification);
                break;
            case REQUEST_ADD_MUSIC_TO_PLAYLIST:
                System.out.println("Add Music To Playlist Notification ");
                parseMusicToAddToPlaylistFromJSON(notification);
                break;
            case SERVER_SHUTDOWN:
                System.out.println("Server Shutdown Notification");
                serverShutdown();
                break;
            default:
                break;
        }
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
            System.out.println("Add Music -> Username: " + username +
                    " MusicName: " + musicName + " Author: " + author + " Year: " + year +
                    " Duration: " + duration + " Genre: " + genre);

            addMusicNotification(username, musicName, author, album, year, duration, genre);
        } else {
            String musicToEdit = music.getString(MUSIC_TO_EDIT);

            System.out.println("Edit Music -> Username: " + username + " MusicToEdit " + musicToEdit +
                    " MusicName: " + musicName + " Author: " + author + " Year: " + year +
                    " Duration: " + duration + " Genre: " + genre);

            editMusicNotification(username, musicToEdit, musicName, author, album, year, duration, genre);
        }
    }

    private void parseMusicToDownloadFromJSON(JSONObject musicToDownload) {
        String musicName = musicToDownload.getString(MUSIC_NAME);
        int port = musicToDownload.getInt(PORT);

        System.out.println("Download Music -> " + musicName + " Port: " + port);

        downloadMusic(musicName, port);
    }

    private void parsePlaylistFromJSON(JSONObject playlist) {
        String username = playlist.getString(USERNAME);
        String playlistName = playlist.getString(PLAYLIST_NAME);

        System.out.println("Add Playlist -> Username: " + username +
                " PlaylistName: " + playlistName);

        addPlaylistNotification(username, playlistName);
    }

    private void parseMusicToAddToPlaylistFromJSON(JSONObject musicToAddToPlaylist) {
        String username = musicToAddToPlaylist.getString(USERNAME);
        String musicName = musicToAddToPlaylist.getString(MUSIC_NAME);
        String playlistName = musicToAddToPlaylist.getString(PLAYLIST_NAME);

        System.out.println("Add Music To Playlist -> Username: " + username +
                " MusicName: " + musicName +
                " PlaylistName: " + playlistName);

        addMusicToPlaylistNotification(musicName, playlistName);
    }

    private boolean isResponseApproved(String responseStatus) {
        if (responseStatus.equals(APPROVED)) {
            return true;
        }
        return false;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void databaseInformation() {
    }

    @Override
    public void addMusicNotification(String username, String name, String author, String album, int year, int duration, String genre) {
        mainController.addMusic(username, name, author, album, year, duration, genre);
        //TODO: Show alert
    }

    @Override
    public void editMusicNotification(String username, String musicToEdit, String name, String author, String album, int year, int duration, String genre) {
        mainController.editMusic(username, musicToEdit, name, author, album, year, duration, genre);
        //TODO: Show alert
    }

    @Override
    public void downloadMusic(String musicName, int port) {
        try {
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
            UploadMusic uploadMusic = new UploadMusic(musicName, new Socket(communicationHandler.getSocketAddress(), port));
            uploadMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPlaylistNotification(String username, String name) {
        mainController.addPlaylist(username, name);
        //TODO: Show alert
    }

    @Override
    public void addMusicToPlaylistNotification(String musicName, String playlistName) {
        try {
            mainController.addMusicToPlaylist(playlistName, musicName);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverShutdown() {
        communicationHandler.shutdown();
        screenController.activate(ScreenController.Screen.LOGIN);
    }
}
