package sample.communication;

import org.json.JSONObject;
import sample.ServerController;
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

    private ServerController serverController;

    private InetAddress serversDirectoryAddress;

    private ServerInformation serverInformation;

    private boolean isRunning = true;

    private DatagramSocket notificationsDatagramSocket;
    private int notificationPort;
    private DatagramSocket requestsDatagramSocket;

    public ServersDirectoryCommunication(String serversDirectoryIP, ServerInformation serverInformation, ServerController serverController) throws NoServersDirectory, IOException {
        this.serverInformation = serverInformation;
        serversDirectoryAddress = InetAddress.getByName(serversDirectoryIP);

        notificationsDatagramSocket = new DatagramSocket();
        notificationsDatagramSocket.setSoTimeout(socketsTimeout);

        connectToServersDirectory(serversDirectoryAddress);

        this.serverController = serverController;

        notificationsDatagramSocket.setSoTimeout(0);
        requestsDatagramSocket = new DatagramSocket();
        requestsDatagramSocket.setSoTimeout(socketsTimeout);
    }

    @Override
    public void connectToServersDirectory(InetAddress serversDirectoryAddress) throws NoServersDirectory {
        System.out.println("Connecting to Servers Directory at " + serversDirectoryAddress.getHostAddress());

        JSONObject request = new JSONObject();
        request.put(REQUEST, SERVER);
        request.put(UDP_PORT, notificationsDatagramSocket.getLocalPort());
        addServerInformationToRequest(serverInformation, request);

        try {
            sendPacketAndWaitForResponse(notificationsDatagramSocket, serversDirectoryAddress, serversDirectoryPort, request);
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

    private JSONObject receiveNotificationRequest() throws IOException {
        byte[] bArray = new byte[datagramPacketSize];
        DatagramPacket datagramPacket = new DatagramPacket(bArray, bArray.length);
        notificationsDatagramSocket.receive(datagramPacket);
        notificationPort = datagramPacket.getPort();
        String jsonRequest = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        return new JSONObject(jsonRequest);
    }

    private void handlerServersDirectoryNotification(JSONObject notification) {
        ServerInformation serverNotification;

        switch (notification.getString(REQUEST)) {
            case PING:
                periodicPing(serverInformation);
                break;
            case SERVER_CONNECTED:
                serverNotification = getServerInformationFromNotification(notification);
                serverConnected(serverNotification);
                System.out.println("Server Connected notification -> Server " + serverNotification + "\n");
                break;
            case SERVER_DISCONNECTED:
                serverNotification = getServerInformationFromNotification(notification);
                serverDisconnected(serverNotification);
                System.out.println("Server Disconnected notification -> Server " + serverNotification + "\n");
                break;
            case PRIMARY_SERVER:
                serverNotification = getServerInformationFromNotification(notification);
                primaryServer(serverNotification);
                System.out.println("Primary Server notification -> Server " + serverNotification + "\n");
                break;
            default:
                System.out.println("Invalid notification!\n");
                break;
        }
    }

    private ServerInformation getServerInformationFromNotification(JSONObject notification) {
        String ip = notification.getString(IP);
        int port = notification.getInt(TCP_PORT);
        return new ServerInformation(ip, port);
    }

    private void sendNotificationResponse() {
        JSONObject response = new JSONObject();
        response.put(IP, serverInformation.getIp());
        response.put(TCP_PORT, serverInformation.getTcpPort());
        try {
            sendRequestToServersDirectory(notificationsDatagramSocket, serversDirectoryAddress, notificationPort, response);
        } catch (IOException e) {
            System.out.println("Could not send notification response to servers directory!\n");
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                System.out.println("Waiting for Servers Directory Notification!\n");
                handlerServersDirectoryNotification(receiveNotificationRequest());
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
            sendPacketAndWaitForResponse(notificationsDatagramSocket, serversDirectoryAddress, notificationPort, request);
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
            System.out.println("Could not send Client Disconnected Request to ServersDirectory!\n");
        }
    }

    @Override
    public void serverConnected(ServerInformation serverInformation) {
        serverController.addServerIP(serverInformation);
        sendNotificationResponse();
    }

    @Override
    public void serverDisconnected(ServerInformation serverInformation) {
        serverController.removeServerIP(serverInformation);
        sendNotificationResponse();
    }

    @Override
    public void primaryServer(ServerInformation serverInformation) {
        if (serverInformation.equals(this.serverInformation)) {
            System.out.println("Setting up as the Primary Server!\n");
            serverController.setupAsPrimaryServer();
        }
        sendNotificationResponse();
    }

    public void shutdown() {
        isRunning = false;
        notificationsDatagramSocket.close();
        requestsDatagramSocket.close();
    }
}

