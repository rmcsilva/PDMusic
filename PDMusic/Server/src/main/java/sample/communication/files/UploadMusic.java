package sample.communication.files;

import sample.files.UploadMusicFile;

import java.io.IOException;
import java.net.Socket;

public class UploadMusic extends UploadMusicFile {

    private Socket socket;

    public UploadMusic(String musicName, Socket socket) throws IOException {
        super(musicName, ServerFileManager.getPdMusic(musicName), socket.getOutputStream());
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
