package sample.communication.files;

import sample.communication.ClientCommunication;
import sample.database.models.Music;
import sample.files.DownloadMusicFile;

import java.io.IOException;
import java.net.Socket;

public class DownloadMusic extends DownloadMusicFile {

    private Socket socket;

    private ClientCommunication clientCommunication;

    private Music music;

    private String musicToEdit;

    //Constructor to add music
    public DownloadMusic(ClientCommunication clientCommunication, Music music, Socket socket) throws IOException {
        super(music.getName(), ServerFileManager.getPdMusicDestination(music.getName()), socket.getInputStream());
        this.clientCommunication = clientCommunication;
        this.music = music;
        this.socket = socket;
        musicToEdit = null;
    }

    //Constructor to edit music
    public DownloadMusic(ClientCommunication clientCommunication, Music music, String musicToEdit, Socket socket) throws IOException {
        super(music.getName(), ServerFileManager.getPdMusicDestination(music.getName()), socket.getInputStream());
        this.clientCommunication = clientCommunication;
        this.music = music;
        this.socket = socket;
        this.musicToEdit = musicToEdit;
    }

    public String getMusicName() {
        return music.getName();
    }

    @Override
    public void run() {
        System.out.println("Starting to download music -> " + music.getName());

        super.run();

        if (hasDownloadSuccessfully()) {
            if (musicToEdit == null) {
                System.out.println("Music downloaded successfully sending add music request to server!");
                clientCommunication.addMusic(music.getName(), music.getAuthor(), music.getAlbum(), music.getYear(),
                        music.getDuration(), music.getGenre());
            } else {
                System.out.println("Music downloaded successfully sending edit music request to server!");
                clientCommunication.editMusic(musicToEdit, music.getName(), music.getAuthor(), music.getAlbum(), music.getYear(),
                        music.getDuration(), music.getGenre());
            }
        } else {
            System.out.println("Music " + music.getName() + " could not be downloaded!");
        }

        clientCommunication.removeMusicFromBeingDownloaded(this);

        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
