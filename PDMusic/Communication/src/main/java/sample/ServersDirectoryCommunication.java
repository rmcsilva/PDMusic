package sample;

import sample.models.ServerInformation;

public interface ServersDirectoryCommunication {

    int PING_LIMIT = 3;
    int notificationsSocketTimout = 2000;

    //JSON key values
    String SERVERS = "servers";
    String UDP_PORT = "udpPort";
    String PING = "periodicPing";
    String CLIENT_DISCONNECTED = "clientDisconnected";
    String SERVER_CONNECTED = "serverConnected";
    String SERVER_DISCONNECTED = "serverDisconnected";
    String PRIMARY_SERVER = "primaryServer";
    String SHUTDOWN_SERVER = "shutdownServer";

    void periodicPing(ServerInformation serverInformation);
    void clientDisconnected(ServerInformation serverInformation);
    void serverConnected(ServerInformation serverInformation);
    void serverDisconnected(ServerInformation serverInformation);
    void primaryServer(ServerInformation serverInformation);
    void shutdownServer(ServerInformation serverInformation);
}
