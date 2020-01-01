package sampleRMI.communication.interfaces;

import java.rmi.Remote;

public interface MonitoringInterface extends Remote {
    void newUser(String user) throws java.rmi.RemoteException;

    void userExit(String user) throws java.rmi.RemoteException;
}
