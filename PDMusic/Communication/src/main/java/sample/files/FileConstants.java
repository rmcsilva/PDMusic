package sample.files;

import static sample.ServersDirectoryInformation.datagramPacketSize;

public interface FileConstants {
    //File chunk
    int TCP_FILE_CHUNK_SIZE = 8192;
    int UDP_FILE_CHUNK_SIZE = datagramPacketSize - 1024;
    //Temp directory property
    String TEMP_DIRECTORY = "java.io.tmpdir";
}
