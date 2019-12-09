package sample;

import org.json.JSONObject;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.*;
import java.util.Comparator;
import java.util.PriorityQueue;

import static sample.JSONConstants.REQUEST;
import static sample.ServersDirectoryInformation.*;
import static sample.ServersDirectoryInformation.PORT;

public class CommunicationHandler extends Thread implements ServersDirectoryCommunication {

    private PriorityQueue<ServerInformation> serversInformation;

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    private boolean isRunning = true;

    public CommunicationHandler() throws SocketException {
        serversInformation = new PriorityQueue<>(Comparator.comparingInt(ServerInformation::getNumberOfClients));
        datagramSocket = new DatagramSocket(serversDirectoryPort);
    }

    private ServerInformation findBestServerForClient() {
        ServerInformation serverInformation = serversInformation.poll();
        assert serverInformation != null;
        serverInformation.newClient();
        serversInformation.add(serverInformation);
        return serverInformation;
    }

    private void removeClientFromServer(ServerInformation affectedServer) {
        for (ServerInformation server : serversInformation) {
            if (server.equals(affectedServer)) {
                server.clientLogout();
                serversInformation.remove(server);
                serversInformation.add(server);
                break;
            }
        }
    }

    public void shutdown() {
        isRunning = false;

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
                serverInformation = getServerInformationFromRequest(request);
                serversInformation.add(serverInformation);
                System.out.print("Server Request, details " + serverInformation + " -> ");
                break;
            case CLIENT:
                if (serversInformation.isEmpty()) break;

                serverInformation = findBestServerForClient();

                response.put(IP, serverInformation.getIp());
                response.put(PORT, serverInformation.getPort());

                putResponseInDatagramPacket(response);

                System.out.print("Client Request, connect to Server " + serverInformation + " -> ");
                break;
            case CLIENT_DISCONNECTED:
                serverInformation = getServerInformationFromRequest(request);
                clientDisconnected(serverInformation);

                System.out.print("Client Disconnected Request, affected Server " + serverInformation + " -> ");
                break;
            default:
                break;
        }
    }

    private ServerInformation getServerInformationFromRequest(JSONObject request) {
        String ip = request.getString(IP);
        int port = request.getInt(PORT);

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
                        + ":" + datagramPacket.getPort()
                );

                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clientDisconnected(ServerInformation serverInformation) {
        removeClientFromServer(serverInformation);
    }
}
