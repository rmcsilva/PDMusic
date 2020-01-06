package sample;

import org.json.JSONObject;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import static sample.JSONConstants.REQUEST;
import static sample.ServersDirectoryCommunication.PING;
import static sample.ServersDirectoryCommunication.PING_LIMIT;
import static sample.ServersDirectoryInformation.*;

public class ServerPingHandler extends Thread {

    private ServerInformation primaryServer = null;

    CommunicationHandler communicationHandler;

    private DatagramSocket datagramSocket;

    private Timer timer;

    public ServerPingHandler(CommunicationHandler communicationHandler) throws SocketException {
        this.communicationHandler = communicationHandler;
        datagramSocket = new DatagramSocket();
        timer = new Timer();
        timer.schedule(new Ping(), 0, //initial delay
                10 * 1000); //subsequent rate
    }

    public synchronized ServerInformation getPrimaryServer() {
        return primaryServer;
    }

    public synchronized void setPrimaryServer(ServerInformation primaryServer) {
        this.primaryServer = primaryServer;
    }

    @Override
    public void run() {
        DatagramPacket datagramPacket;
        try {
            while (true) {
                byte[] bArray = new byte[datagramPacketSize];
                datagramPacket = new DatagramPacket(bArray, bArray.length);

                datagramSocket.receive(datagramPacket);

                String jsonRequest = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

                JSONObject request = new JSONObject(jsonRequest);

                if (request.getString(REQUEST).equals(PING)) {
                    ServerInformation serverInformation = communicationHandler.getServerTCPInformationFromRequest(request);

                    communicationHandler.periodicPing(serverInformation);

                    datagramSocket.send(datagramPacket);

                    //Check if it needs to setup the primary server
                    if (primaryServer == null) {
                        setPrimaryServer(serverInformation);
                        System.out.println("Server " + serverInformation + " is the Primary Server!\n");
                        CompletableFuture.runAsync(() -> communicationHandler.primaryServer(primaryServer));
                    }

                } else {
                    System.out.println("Unrecognized request type!\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pingServers() {
        JSONObject request = new JSONObject();
        request.put(REQUEST, PING);

        byte[] bArray = request.toString().getBytes();
        DatagramPacket datagramPacket;

        for (ServerInformation server : communicationHandler.getServersInformation()) {
            try {
                datagramPacket = new DatagramPacket(bArray, bArray.length, server.getAddress(), server.getUdpPort());
                datagramSocket.send(datagramPacket);

                server.pingServer();
                System.out.println("Pinging Server -> " + server +
                        ", current counter: " + server.getPingCounter());
                if (server.getPingCounter() > PING_LIMIT) {
                    System.out.println("Server " + server + " exceeded the ping limit!\n");
                    CompletableFuture.runAsync(() -> communicationHandler.serverDisconnected(server));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Pinged all servers!\n");
    }

    void shutdown() {
        datagramSocket.close();
        timer.cancel();
    }

    private class Ping extends TimerTask {
        @Override
        public void run() {
            pingServers();
        }
    }
}
