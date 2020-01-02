package sample.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SDNotificationInterface extends Remote {
    void showNotification(String message) throws RemoteException;
}
