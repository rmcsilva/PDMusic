package sample.rmi;

import sample.CommunicationHandler;
import sample.models.ServerInformation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RegistrySDService extends UnicastRemoteObject implements RegistrySDInterface {

    public static final String REGISTRY_SERVICE_NAME = "RegistryDS";

    private CommunicationHandler communicationHandler;

    public RegistrySDService(CommunicationHandler communicationHandler) throws RemoteException {
        this.communicationHandler = communicationHandler;
    }

    @Override
    public List<String> listServersInformation() throws RemoteException {
        List<String> servers = new ArrayList<>();
        for(ServerInformation serverInformation : communicationHandler.getServersInformation()) {
            servers.add(serverInformation.toString());
        }
        return servers;
    }
}
