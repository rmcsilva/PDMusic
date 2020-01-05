package sample.rmi;

import sample.CommunicationHandler;
import sample.models.ServerInformation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class RegistrySDService extends UnicastRemoteObject implements RegistrySDInterface {

    public static final String REGISTRY_SERVICE_NAME = "RegistryDS";

    private CommunicationHandler communicationHandler;

    private List<SDNotificationInterface> sdNotificationInterfaces;

    public RegistrySDService(CommunicationHandler communicationHandler) throws RemoteException {
        this.communicationHandler = communicationHandler;
        sdNotificationInterfaces = new ArrayList<>();
    }

    public void sendNotificationToListeners(String message) {
        ListIterator<SDNotificationInterface> listIterator = sdNotificationInterfaces.listIterator();

        while (listIterator.hasNext()) {
            SDNotificationInterface listener = listIterator.next();
            try {
                listener.showNotification(message);
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("Could not send notification to observer! Removing from list!");
                listIterator.remove();
            }
        }
    }

    @Override
    public synchronized List<String> listServersInformation() throws RemoteException {
        List<String> servers = new ArrayList<>();
        for(ServerInformation serverInformation : communicationHandler.getServersInformation()) {
            servers.add(serverInformation.toString());
        }
        return servers;
    }

    @Override
    public synchronized boolean shutdownServer(String ip, int port) throws RemoteException {
        return communicationHandler.shutdownServer(ip, port);
    }

    @Override
    public synchronized void addListener(SDNotificationInterface sdNotificationInterface) {
        sdNotificationInterfaces.add(sdNotificationInterface);
    }

    @Override
    public synchronized void removeListener(SDNotificationInterface sdNotificationInterface) {
        sdNotificationInterfaces.remove(sdNotificationInterface);
    }
}
