package sample.communication.files;

import sample.files.DownloadMusicFile;

import java.io.IOException;
import java.net.Socket;

public class DownloadMusic extends DownloadMusicFile {

    private Socket socket;

    public DownloadMusic(String musicName, Socket socket) throws IOException {
        super(musicName, ServerFileManager.getPdMusicDestination(musicName), socket.getInputStream());
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
