package sample;

import sample.models.ServerInformation;

public interface ServersDirectoryCommunication {

    int PING_LIMIT = 3;

    //JSON key values
    String UDP_PORT = "udpPort";
    String PING = "periodicPing";
    String CLIENT_DISCONNECTED = "clientDisconnected";
    String SERVER_DISCONNECTED = "serverDisconnected";

    void periodicPing(ServerInformation serverInformation);
    void clientDisconnected(ServerInformation serverInformation);
    void serverDisconnected(ServerInformation serverInformation);
}
