package sample;

import org.json.JSONObject;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static sample.JSONConstants.REQUEST;
import static sample.ServersDirectoryInformation.*;
import static sample.ServersDirectoryInformation.PORT;

public class CommunicationHandler extends Thread {

    private PriorityQueue<ServerInformation> serversInformation;

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    private boolean isRunning = true;

    public CommunicationHandler() throws SocketException {
        serversInformation = new PriorityQueue<>(Comparator.comparingInt(ServerInformation::getNumberOfClients));
        datagramSocket = new DatagramSocket(serversDirectoryPort);
    }

    public ServerInformation findBestServerForClient() {
        ServerInformation serverInformation = serversInformation.poll();
        assert serverInformation != null;
        serverInformation.newClient();
        serversInformation.add(serverInformation);
        return serverInformation;
    }

    public void shutdown() {
        isRunning = false;

        try {
            datagramSocket.setSoTimeout(socketsTimeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
                JSONObject response = new JSONObject();

                switch (request.getString(REQUEST)) {
                    case SERVER:
                        String ip = request.getString(IP);
                        int port = request.getInt(PORT);

                        serversInformation.add(new ServerInformation(ip, port));
                        //TODO: Send response
                        System.out.print("Server request -> ");
                        break;
                    case CLIENT:
                        if (serversInformation.isEmpty()) break;
            
                        ServerInformation serverInformation = findBestServerForClient();

                        response.put(IP, serverInformation.getIp());
                        response.put(PORT, serverInformation.getPort());

                        bArray = response.toString().getBytes();
                        datagramPacket.setData(bArray, 0, bArray.length);

                        System.out.print("Client request -> ");
                        break;
                    default:
                        break;
                }

                System.out.println("Packet received from " + datagramPacket.getAddress().getHostAddress()
                        + ":" + datagramPacket.getPort()
                );

                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
