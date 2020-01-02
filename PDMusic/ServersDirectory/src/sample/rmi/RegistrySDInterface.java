package sample.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RegistrySDInterface extends Remote {
    List<String> listServersInformation() throws RemoteException;
    boolean shutdownServer(String ip, int port) throws RemoteException;
}
