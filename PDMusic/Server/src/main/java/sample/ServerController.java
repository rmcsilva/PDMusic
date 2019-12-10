package sample;

import sample.communication.ClientCommunication;
import sample.communication.ClientNotificationsHandler;
import sample.communication.ServersDirectoryCommunication;
import sample.exceptions.NoServersDirectory;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ServerController extends Thread {

    ServerSocket serverSocket;
    ServerInformation serverInformation;

    boolean isServerRunning = true;

    boolean isPrimaryServer = false;

    HashSet<ServerInformation> servers;

    ServersDirectoryCommunication serversDirectoryCommunication;

    ClientNotificationsHandler clientNotificationsHandler;

    public ServerController(String serversDirectoryIP) throws IOException, NoServersDirectory {
        startServer();
        serversDirectoryCommunication = new ServersDirectoryCommunication(serversDirectoryIP, serverInformation, this);
        serversDirectoryCommunication.setDaemon(true);
        serversDirectoryCommunication.start();
        clientNotificationsHandler = new ClientNotificationsHandler(this);
        servers = new HashSet<>();
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

    public HashSet<ServerInformation> getServers() {
        return servers;
    }

    public synchronized void addServerIP(ServerInformation server) {
        servers.add(server);
    }

    public synchronized void removeServerIP(ServerInformation server) {
        servers.remove(server);
    }

    public void setupAsPrimaryServer() {
        isPrimaryServer = true;
    }

    @Override
    public void run() {
        while (isServerRunning) {
            System.out.println("Waiting for a Client to connect!\n");

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

    public void shutdown() {
        isServerRunning = false;

        serversDirectoryCommunication.shutdown();
        clientNotificationsHandler.serverShutdown();

        try {
            Socket socket = new Socket(serverInformation.getIp(), serverInformation.getTcpPort());
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
