package sample.communication.files;

import sample.communication.ClientCommunication;
import sample.files.UploadMusicFile;

import java.io.IOException;
import java.net.Socket;

public class UploadMusic extends UploadMusicFile {

    private ClientCommunication clientCommunication;

    private String musicName;

    private Socket socket;

    public UploadMusic(ClientCommunication clientCommunication, String musicName, Socket socket) throws IOException {
        super(musicName, ServerFileManager.getPdMusic(musicName), socket.getOutputStream());
        this.clientCommunication = clientCommunication;
        this.musicName = musicName;
        this.socket = socket;
    }

    public String getMusicName() {
        return musicName;
    }

    @Override
    public void run() {
        super.run();
        clientCommunication.removeMusicFromBeingUploaded(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
