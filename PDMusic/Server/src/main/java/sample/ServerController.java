package sample;

import sample.communication.ClientCommunication;
import sample.communication.ClientNotifications;
import sample.communication.ServersDirectoryCommunication;
import sample.exceptions.CountExceededException;
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

    public ServerController(String serversDirectoryIP) throws IOException, CountExceededException {
        startServer();
        serversDirectoryCommunication = new ServersDirectoryCommunication(serversDirectoryIP, serverInformation);
    }

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(0);

        String serverAddress = InetAddress.getLocalHost().getHostAddress();
        int serverPort = serverSocket.getLocalPort();

        System.out.println("Server running at " + InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort());

        serverInformation = new ServerInformation(serverAddress, serverPort);
    }

    @Override
    public void run() {
        while (isServerRunning) {
            System.out.println("Connecting to client");

            try {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new ClientCommunication(socket));
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
