package sample;

import sample.models.ServerInformation;

public interface ServersDirectoryCommunication {
    //JSON key values
    String CLIENT_DISCONNECTED = "clientDisconnected";

    void clientDisconnected(ServerInformation serverInformation);
}
