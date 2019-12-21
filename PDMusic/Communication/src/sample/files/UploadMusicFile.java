package sample.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static sample.files.FileConstants.TCP_FILE_CHUNK_SIZE;

public class UploadMusicFile extends Thread {

    String musicName;

    FileInputStream musicFileInputStream;

    OutputStream outputStream;

    public UploadMusicFile(String musicName, FileInputStream musicFileInputStream, OutputStream outputStream) {
        this.musicName = musicName;
        this.musicFileInputStream = musicFileInputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        try {
            int nBytes;
            byte []fileChunk = new byte[TCP_FILE_CHUNK_SIZE];

            while((nBytes = musicFileInputStream.read(fileChunk)) > 0){
                outputStream.write(fileChunk, 0, nBytes);
            }

            System.out.println("Music " + musicName + " uploaded successfully");

            musicFileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
