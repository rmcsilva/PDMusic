package sample.controllers.communication.files;

import sample.files.FileManager;

public final class ClientFileManager extends FileManager {
    private static final String musicFolderName = "PDMusic";

    static {
        initFolder(musicFolderName);
    }
}
