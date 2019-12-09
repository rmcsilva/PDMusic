package sample;

import org.json.JSONObject;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.*;
import java.util.Comparator;
import java.util.PriorityQueue;

import static sample.JSONConstants.REQUEST;
import static sample.ServersDirectoryInformation.*;
import static sample.ServersDirectoryInformation.TCP_PORT;

public class CommunicationHandler extends Thread implements ServersDirectoryCommunication {

    private PriorityQueue<ServerInformation> serversInformation;

    private ServerPingHandler serverPingHandler;

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    private boolean isRunning = true;

    public CommunicationHandler() throws SocketException {
        serversInformation = new PriorityQueue<>(Comparator.comparingInt(ServerInformation::getNumberOfClients));
        datagramSocket = new DatagramSocket(serversDirectoryPort);
        serverPingHandler = new ServerPingHandler(this);
        serverPingHandler.start();
        System.out.println("Starting Server Periodic Ping!\n");
    }

    public synchronized PriorityQueue<ServerInformation> getServersInformation() {
        return serversInformation;
    }

    private synchronized ServerInformation findBestServerForClient() {
        ServerInformation serverInformation = serversInformation.poll();
        assert serverInformation != null;
        serverInformation.newClient();
        serversInformation.add(serverInformation);
        return serverInformation;
    }

    private synchronized void removeClientFromServer(ServerInformation affectedServer) {
        for (ServerInformation server : getServersInformation()) {
            if (server.equals(affectedServer)) {
                server.clientLogout();
                serversInformation.remove(server);
                serversInformation.add(server);
                return;
            }
        }
    }

    private synchronized boolean removeServerFromQueue(ServerInformation serverToRemove) {
        for (ServerInformation server : getServersInformation()) {
            if (server.equals(serverToRemove)) {
                serversInformation.remove(server);
                return true;
            }
        }
        return false;
    }

    public void shutdown() {
        isRunning = false;

        serverPingHandler.shutdown();

        try {
            datagramSocket.setSoTimeout(socketsTimeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        ServerInformation serverInformation;

        switch (request.getString(REQUEST)) {
            case SERVER:
                serverInformation = getServerFullInformationFromRequest(request);
                serversInformation.add(serverInformation);
                System.out.print("Server Request, details " + serverInformation + " -> ");
                break;
            case CLIENT:
                if (serversInformation.isEmpty()) break;

                serverInformation = findBestServerForClient();

                response.put(IP, serverInformation.getIp());
                response.put(TCP_PORT, serverInformation.getTcpPort());

                putResponseInDatagramPacket(response);

                System.out.print("Client Request, connect to Server " + serverInformation + " -> ");
                break;
            case CLIENT_DISCONNECTED:
                serverInformation = getServerTCPInformationFromRequest(request);
                clientDisconnected(serverInformation);

                System.out.print("Client Disconnected Request, affected Server " + serverInformation + " -> ");
                break;
            default:
                break;
        }
    }

    ServerInformation getServerFullInformationFromRequest(JSONObject request) {
        String ip = request.getString(IP);
        int tcpPort = request.getInt(TCP_PORT);
        int udpPort = request.getInt(UDP_PORT);

        return new ServerInformation(ip, tcpPort, udpPort);
    }

    ServerInformation getServerTCPInformationFromRequest(JSONObject request) {
        String ip = request.getString(IP);
        int port = request.getInt(TCP_PORT);

        return new ServerInformation(ip, port);
    }

    private void putResponseInDatagramPacket(JSONObject response) {
        byte[] bArray = response.toString().getBytes();
        datagramPacket.setData(bArray, 0, bArray.length);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                byte[] bArray = new byte[datagramPacketSize];
                datagramPacket = new DatagramPacket(bArray, bArray.length);

                datagramSocket.receive(datagramPacket);

                String jsonRequest = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

                JSONObject request = new JSONObject(jsonRequest);

                handleRequest(request);

                System.out.println("Packet received from " + datagramPacket.getAddress().getHostAddress()
                        + ":" + datagramPacket.getPort() + "\n"
                );

                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void periodicPing(ServerInformation serverInformation) {
        for (ServerInformation server : getServersInformation()) {
            if (server.equals(serverInformation)) {
                server.resetPingCounter();
                System.out.println("Got ping response from Server -> " + server);
                return;
            }
        }
    }

    @Override
    public void clientDisconnected(ServerInformation serverInformation) {
        removeClientFromServer(serverInformation);
    }

    @Override
    public void serverDisconnected(ServerInformation serverInformation) {
        if (removeServerFromQueue(serverInformation)) {
            //TODO: Warn other servers
            System.out.println("Server " + serverInformation + " is no longer active!");
        }
    }
}
