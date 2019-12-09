package sample;

import sample.communication.ClientCommunication;
import sample.communication.ClientNotificationsHandler;
import sample.communication.ServersDirectoryCommunication;
import sample.exceptions.CountExceededException;
import sample.exceptions.NoServersDirectory;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController extends Thread {

    ServerSocket serverSocket;
    ServerInformation serverInformation;

    boolean isServerRunning = true;

    ServersDirectoryCommunication serversDirectoryCommunication;

    ClientNotificationsHandler clientNotificationsHandler;

    public ServerController(String serversDirectoryIP) throws IOException, NoServersDirectory {
        startServer();
        serversDirectoryCommunication = new ServersDirectoryCommunication(serversDirectoryIP, serverInformation);
        clientNotificationsHandler = new ClientNotificationsHandler(this);
    }

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(0);

        String serverAddress = InetAddress.getLocalHost().getHostAddress();
        int serverPort = serverSocket.getLocalPort();

        System.out.println("Server running at " + InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort());

        serverInformation = new ServerInformation(serverAddress, serverPort);
    }

    public void clientLoggedOut() {
        serversDirectoryCommunication.clientDisconnected(serverInformation);
    }

    @Override
    public void run() {
        while (isServerRunning) {
            System.out.println("Waiting for a Client to connect!");

            try {
                Socket socket = serverSocket.accept();
                ClientCommunication client = new ClientCommunication(socket, clientNotificationsHandler);
                clientNotificationsHandler.addClient(client);
                Thread thread = new Thread(client);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown() {
        isServerRunning = false;

        try {
            Socket socket = new Socket(serverInformation.getIp(), serverInformation.getPort());
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
