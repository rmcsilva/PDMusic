package sample.models;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerInformation {

    private String ip;
    private int port;
    private int numberOfClients;

    public ServerInformation(String ip, int port) {
        this.ip = ip;
        this.port = port;
        numberOfClients = 0;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() throws UnknownHostException {
        return InetAddress.getByName(ip);
    }

    public void newClient() {
        ++numberOfClients;
    }

    public void clientLogout() {
        --numberOfClients;
    }
}
