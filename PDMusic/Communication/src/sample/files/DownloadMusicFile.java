package sample.files;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

import static sample.files.FileConstants.TCP_FILE_CHUNK_SIZE;
import static sample.files.FileConstants.TEMP_DIRECTORY;

public class DownloadMusicFile extends Thread {

    FileOutputStream musicFileOutputStream;

    String musicName;

    Path musicTempPath;
    Path musicDestinationPath;

    InputStream inputStream;

    public DownloadMusicFile(String musicName, String musicDestination, InputStream inputStream) throws IOException {
        this.musicName = musicName;
        //Download file to Temp Directory first
        String tempDirectory = System.getProperty(TEMP_DIRECTORY);
        musicTempPath = Paths.get(tempDirectory + File.separator + musicName);
        musicFileOutputStream = new FileOutputStream(musicTempPath.toFile());
        //Get destination to the path where the music will be copied after the download finished
        musicDestinationPath = Paths.get(musicDestination);
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            int nBytes;
            byte []fileChunk = new byte[TCP_FILE_CHUNK_SIZE];

            while((nBytes = inputStream.read(fileChunk)) > 0){
                musicFileOutputStream.write(fileChunk, 0, nBytes);
            }

            System.out.println("Music " + musicName + " downloaded successfully");

            Files.move(musicTempPath, musicDestinationPath, StandardCopyOption.REPLACE_EXISTING);
            musicFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                musicFileOutputStream.close();
            } catch (IOException ignored) {}
        }
    }
}
