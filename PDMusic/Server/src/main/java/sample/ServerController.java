package sample;

import sample.communication.ClientCommunication;
import sample.communication.ClientNotificationsHandler;
import sample.communication.ServerCommunication;
import sample.communication.ServersDirectoryCommunication;
import sample.communication.files.ServerFileManager;
import sample.exceptions.NoServersDirectory;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ServerController extends Thread {

    private ServerSocket serverSocket;
    private ServerInformation serverInformation;

    private boolean isServerRunning = true;

    private HashSet<ServerInformation> servers;

    private ServerCommunication serverCommunication;

    private ServersDirectoryCommunication serversDirectoryCommunication;

    private ClientNotificationsHandler clientNotificationsHandler;

    public ServerController(String serversDirectoryIP, String nic) throws IOException, NoServersDirectory {
        //Setup server music files location
        new ServerFileManager();

        startServer();

        serversDirectoryCommunication = new ServersDirectoryCommunication(serversDirectoryIP, this);
        serversDirectoryCommunication.setDaemon(true);
        serversDirectoryCommunication.start();

        servers = new HashSet<>();
        serverCommunication = new ServerCommunication(this, nic);
        serverCommunication.start();

        clientNotificationsHandler = new ClientNotificationsHandler(this);
    }

    public ServerInformation getServerInformation() {
        return serverInformation;
    }

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(0);

        String serverAddress = InetAddress.getLocalHost().getHostAddress();
        int serverPort = serverSocket.getLocalPort();

        System.out.println("Server running at " + InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort());

        serverInformation = new ServerInformation(serverAddress, serverPort);
    }

    public boolean isServerRunning() {
        return isServerRunning;
    }

    public void clientLoggedOut() {
        serversDirectoryCommunication.clientDisconnected(serverInformation);
    }

    public synchronized HashSet<ServerInformation> getServers() {
        return servers;
    }

    public synchronized void addServerIP(ServerInformation server) {
        System.out.println("Added server " + server);
        servers.add(server);
    }

    public synchronized void removeServerIP(ServerInformation server) {
        servers.remove(server);
    }

    public void setupAsPrimaryServer() {
        serverCommunication.setupAsPrimaryServer();
    }

    @Override
    public void run() {
        while (isServerRunning) {
            System.out.println("Waiting for a Client to connect!\n");

            try {
                Socket socket = serverSocket.accept();
                ClientCommunication client = new ClientCommunication(socket, clientNotificationsHandler, serverCommunication);
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
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
