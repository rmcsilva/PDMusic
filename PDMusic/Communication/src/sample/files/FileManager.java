package sample.files;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {
    private static String musicFolderPathName;

    private static final String musicFormat = ".mp3";

    protected static void initFolder(String musicFolderName) {
        FileManager.musicFolderPathName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + musicFolderName;
        try {
            Files.createDirectories(Paths.get(musicFolderPathName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileInputStream getPdMusic(String musicName) throws IOException {
        String musicPath = new File(musicFolderPathName + File.separator + musicName + musicFormat).getCanonicalPath();
        return new FileInputStream(musicPath);
    }

    public static String getPdMusicDestination(String musicName) throws IOException {
        //TODO: Check if file already exists
        return musicFolderPathName + File.separator + musicName + musicFormat;
    }
}