package sample;

import java.io.IOException;

public interface ServersDirectoryInformation {

    int datagramPacketSize = 4096;
    int serversDirectoryPort = 5001;
    int socketsTimeout = 10000;

    //JSON key values
    String CLIENT = "client";
    String SERVER = "server";
    String IP = "ip";
    String PORT = "port";

    void connectToServersDirectory(String sdIp) throws IOException;

}
