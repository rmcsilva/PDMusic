package sample;

import sample.exceptions.NoServersDirectory;

import java.net.InetAddress;

public interface ServersDirectoryInformation {

    int datagramPacketSize = 4096;
    int serversDirectoryPort = 5001;
    int socketsTimeout = 10000;

    int NUMBER_OF_CONNECTION_ATTEMPTS = 3;

    //JSON key values
    String CLIENT = "client";
    String SERVER = "server";
    String IP = "ip";
    String TCP_PORT = "tcpPort";

    void connectToServersDirectory(InetAddress serversDirectoryAddress) throws NoServersDirectory;

}
