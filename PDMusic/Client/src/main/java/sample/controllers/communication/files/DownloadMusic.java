package sample.controllers.communication.files;

import javafx.application.Platform;
import sample.controllers.ScreenController;
import sample.files.DownloadMusicFile;

import java.io.IOException;
import java.net.Socket;

public class DownloadMusic extends DownloadMusicFile {

    private Socket socket;

    private String musicName;

    private ScreenController screenController;

    public DownloadMusic(String musicName, Socket socket) throws IOException {
        super(musicName, ClientFileManager.getPdMusicDestination(musicName), socket.getInputStream());
        this.musicName = musicName;
        this.socket = socket;
        screenController = ScreenController.getInstance();
    }

    @Override
    public void run() {
        String music = "Music " + musicName;

        showDialog("Music Download Started", music + " started downloading!\n");

        super.run();

        if (hasDownloadSuccessfully()) {
            showDialog("Music Download Ended", music + " finished downloading successfully!\n");
        } else {
            showDialog("Music Download Failed", music + " could not be downloaded!\nPlease try again!\n");
        }

        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    private void showDialog(String heading, String body) {
        Platform.runLater(() -> screenController.showDialog(heading, body));
    }
}
