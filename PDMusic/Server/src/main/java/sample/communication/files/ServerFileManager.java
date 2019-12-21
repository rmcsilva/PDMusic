package sample.communication.files;

import sample.files.FileManager;

import java.io.File;

public final class ServerFileManager extends FileManager {
    private static final String musicFolderName = "PDMusic" + File.separator + "Server";

    static {
        initFolder(musicFolderName);
    }
}
