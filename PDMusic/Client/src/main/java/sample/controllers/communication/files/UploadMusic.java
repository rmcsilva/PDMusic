package sample.controllers.communication.files;

import javafx.application.Platform;
import sample.controllers.ScreenController;
import sample.files.UploadMusicFile;

import java.io.IOException;
import java.net.Socket;

public class UploadMusic extends UploadMusicFile {

    private Socket socket;

    private String musicName;

    private ScreenController screenController;

    public UploadMusic(String musicName, Socket socket) throws IOException {
        super(musicName, ClientFileManager.getPdMusic(musicName), socket.getOutputStream());
        this.musicName = musicName;
        this.socket = socket;
        screenController = ScreenController.getInstance();
    }

    @Override
    public void run() {
        String music = "Music " + musicName;
        showDialog("Music Upload Started", music + " started uploading!\n");
        super.run();
        showDialog("Music Upload Ended", music + " finished uploading!\n");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDialog(String heading, String body) {
        Platform.runLater(() -> screenController.showDialog(heading, body));
    }
}
