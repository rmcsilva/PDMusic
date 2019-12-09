package sample.communication;

import org.json.JSONObject;
import sample.ServersDirectoryInformation;
import sample.exceptions.CountExceededException;
import sample.exceptions.NoServersDirectory;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static sample.JSONConstants.REQUEST;

public class ServersDirectoryCommunication extends Thread implements ServersDirectoryInformation, sample.ServersDirectoryCommunication {

    private InetAddress serversDirectoryAddress;

    private ServerInformation serverInformation;

    private boolean isRunning = true;

    private DatagramSocket pingDatagramSocket;
    private int pingPort;
    private DatagramSocket requestsDatagramSocket;

    public ServersDirectoryCommunication(String serversDirectoryIP, ServerInformation serverInformation) throws NoServersDirectory, IOException {
        this.serverInformation = serverInformation;
        serversDirectoryAddress = InetAddress.getByName(serversDirectoryIP);

        pingDatagramSocket = new DatagramSocket();
        pingDatagramSocket.setSoTimeout(socketsTimeout);

        connectToServersDirectory(serversDirectoryAddress);

        pingDatagramSocket.setSoTimeout(0);
        requestsDatagramSocket = new DatagramSocket();
        requestsDatagramSocket.setSoTimeout(socketsTimeout);
    }

    @Override
    public void connectToServersDirectory(InetAddress serversDirectoryAddress) throws NoServersDirectory {
        System.out.println("Connecting to Servers Directory at " + serversDirectoryAddress.getHostAddress());

        JSONObject request = new JSONObject();
        request.put(REQUEST, SERVER);
        request.put(UDP_PORT, pingDatagramSocket.getLocalPort());
        addServerInformationToRequest(serverInformation, request);

        try {
            sendPacketAndWaitForResponse(pingDatagramSocket, serversDirectoryAddress, serversDirectoryPort, request);
        } catch (CountExceededException e) {
            throw new NoServersDirectory();
        }
    }

    private void addServerInformationToRequest(ServerInformation serverInformation, JSONObject request) {
        request.put(IP, serverInformation.getIp());
        request.put(TCP_PORT, serverInformation.getTcpPort());
    }

    private void sendPacketAndWaitForResponse(DatagramSocket datagramSocket, InetAddress serversDirectoryAddress, int port, JSONObject request) throws CountExceededException {
        CountExceededException countExceededException = new CountExceededException(NUMBER_OF_CONNECTION_ATTEMPTS);

        while (true) {
            try {
                sendRequestToServersDirectory(datagramSocket, serversDirectoryAddress, port, request);
                receiveServersDirectoryResponse(datagramSocket);
                return;
            } catch (IOException e) {
                System.out.println("Could not connect to serversDirectory! Counter: "
                        + countExceededException.getCounter() +
                        " Limit: " + countExceededException.getLimit()
                );
                countExceededException.incrementCounter();
            }
        }
    }

    private void sendRequestToServersDirectory(DatagramSocket datagramSocket, InetAddress serversDirectoryAddress, int port, JSONObject request) throws IOException {
        byte[] bArray = request.toString().getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(bArray, bArray.length, serversDirectoryAddress, port);
        datagramSocket.send(datagramPacket);
    }

    private void receiveServersDirectoryResponse(DatagramSocket datagramSocket) throws IOException {
        System.out.println("Waiting for ServersDirectory Response...");

        byte[] bArray = new byte[datagramPacketSize];
        DatagramPacket datagramPacket = new DatagramPacket(bArray, bArray.length);
        datagramSocket.receive(datagramPacket);
    }

    private int receivePingRequest() throws IOException {
        byte[] bArray = new byte[datagramPacketSize];
        DatagramPacket datagramPacket = new DatagramPacket(bArray, bArray.length);
        pingDatagramSocket.receive(datagramPacket);
        return datagramPacket.getPort();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                System.out.println("Waiting for Servers Directory Periodic Ping!\n");
                pingPort = receivePingRequest();
                periodicPing(serverInformation);
            } catch (IOException ignored) {}
        }
    }

    @Override
    public void periodicPing(ServerInformation serverInformation) {
        JSONObject request = new JSONObject();
        request.put(REQUEST, PING);
        addServerInformationToRequest(serverInformation, request);

        try {
            System.out.println("Sending Periodic Ping Response to Servers Directory!");
            sendPacketAndWaitForResponse(pingDatagramSocket, serversDirectoryAddress, pingPort, request);
            System.out.println("Servers Directory Confirmed Periodic Ping!\n");
        } catch (CountExceededException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clientDisconnected(ServerInformation serverInformation) {
        JSONObject request = new JSONObject();
        request.put(REQUEST, CLIENT_DISCONNECTED);
        addServerInformationToRequest(serverInformation, request);

        try {
            System.out.println("Sending Client Disconnected Request to Servers Directory!");
            sendPacketAndWaitForResponse(requestsDatagramSocket, serversDirectoryAddress, serversDirectoryPort, request);
            System.out.println("Servers Directory Confirmed Client Disconnected Request!\n");
        } catch (CountExceededException e) {
            System.out.println("Could not send Client Disconnected Request to ServersDirectory!");
        }
    }

    @Override
    public void serverDisconnected(ServerInformation serverInformation) {

    }

    public void shutdown() {
        isRunning = false;
        pingDatagramSocket.close();
        requestsDatagramSocket.close();
    }
}

