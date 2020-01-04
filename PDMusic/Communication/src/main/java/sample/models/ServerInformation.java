package sample.models;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;

public class ServerInformation implements Comparable<ServerInformation> {

    private String ip;
    private int tcpPort, udpPort;
    private int numberOfClients, pingCounter;

    public ServerInformation(String ip, int tcpPort) {
        this.ip = ip;
        this.tcpPort = tcpPort;
        numberOfClients = pingCounter = 0;
    }

    public ServerInformation(String ip, int tcpPort, int udpPort) {
        this.ip = ip;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        numberOfClients = pingCounter = 0;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public int getPingCounter() {
        return pingCounter;
    }

    public String getIp() {
        return ip;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public InetAddress getAddress() throws UnknownHostException {
        return InetAddress.getByName(ip);
    }

    public void resetPingCounter() {
        pingCounter = 0;
    }

    public void pingServer() {
        ++pingCounter;
    }

    public void newClient() {
        ++numberOfClients;
    }

    public void clientLogout() {
        --numberOfClients;
    }

    @Override
    public String toString() {
        return ip + ":" + tcpPort;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ServerInformation)) {
            return false;
        }

        ServerInformation serverInformation = (ServerInformation) obj;

        return ip.equals(serverInformation.getIp()) && tcpPort == serverInformation.getTcpPort();
    }

    @Override
    public int hashCode() {
        return udpPort * ip.hashCode();
    }

    @Override
    public int compareTo(ServerInformation o) {
        return getNumberOfClients() - o.getNumberOfClients();
    }
}
