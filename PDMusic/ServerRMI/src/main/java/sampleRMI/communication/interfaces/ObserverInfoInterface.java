package sampleRMI.communication.interfaces;

import sampleRMI.communication.clientInfoRmi;

import java.rmi.Remote;
import java.util.ArrayList;

public interface ObserverInfoInterface extends Remote {
    void setListenner(MonitoringInterface service) throws java.rmi.RemoteException;

    ArrayList<clientInfoRmi> getUserOnInfo() throws java.rmi.RemoteException;
}
