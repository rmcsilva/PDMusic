package sample.controllers.communication;

import org.json.JSONObject;
import sample.Notifications;
import sample.controllers.MainController;
import sample.controllers.ScreenController;

import java.util.NoSuchElementException;

public class NotificationHandler implements Notifications {

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
                    parseMusicFromJSON(response);
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
    }

    private void parseMusicFromJSON(JSONObject music) {
        String username = music.getString(USERNAME);
        String musicName = music.getString(MUSIC_NAME);
        String author = music.getString(AUTHOR);
        String album = music.getString(ALBUM);
        int year = music.getInt(YEAR);
        int duration = music.getInt(DURATION);
        String genre = music.getString(GENRE);

        System.out.println("Add Music -> " + "Username: " + username +
                " MusicName: " + musicName + " Author: " + author + " Year: " + year +
                " Duration: " + duration + " Genre: " + genre);

        addMusicNotification(username, musicName, author, album, year, duration, genre);
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
    }
}
