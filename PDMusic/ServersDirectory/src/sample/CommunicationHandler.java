package sample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sample.exceptions.CountExceededException;
import sample.models.ServerInformation;
import sample.rmi.RegistrySDService;

import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.PriorityBlockingQueue;

import static sample.JSONConstants.REQUEST;
import static sample.ServersDirectoryInformation.*;
import static sample.ServersDirectoryInformation.TCP_PORT;

public class CommunicationHandler extends Thread implements ServersDirectoryCommunication {

    private PriorityBlockingQueue<ServerInformation> serversInformation;

    private ServerPingHandler serverPingHandler;

    private DatagramSocket requestsDatagramSocket;
    private DatagramSocket notificationsDatagramSocket;
    private DatagramPacket datagramPacket;

    private boolean isRunning = true;

    private RegistrySDService registrySDService;

    public CommunicationHandler(String rmiIP) throws SocketException, RemoteException, MalformedURLException {
        serversInformation = new PriorityBlockingQueue<>();
        requestsDatagramSocket = new DatagramSocket(serversDirectoryPort);
        serverPingHandler = new ServerPingHandler(this);
        serverPingHandler.start();
        System.out.println("Starting Server Periodic Ping!\n");
        notificationsDatagramSocket = new DatagramSocket();
        notificationsDatagramSocket.setSoTimeout(notificationsSocketTimout);
        setupRegistrySDService(rmiIP);
    }

    private void setupRegistrySDService(String rmiIP) throws RemoteException, MalformedURLException {
        System.setProperty("java.rmi.server.hostname", rmiIP);

        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            System.out.println("Creating Registry At Port:" + Registry.REGISTRY_PORT);
        } catch (RemoteException e) {
            System.out.println("Registry Already Running At Port:" + Registry.REGISTRY_PORT);
        }

        registrySDService = new RegistrySDService(this);

        Naming.rebind(RegistrySDService.REGISTRY_SERVICE_NAME, registrySDService);
    }

    public synchronized PriorityBlockingQueue<ServerInformation> getServersInformation() {
        return serversInformation;
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
                return;
            }
        }
    }

    private boolean removeServerFromQueue(ServerInformation serverToRemove) {
        for (ServerInformation server : serversInformation) {
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

        notificationsDatagramSocket.close();
        requestsDatagramSocket.close();


        try {
            //Remove service instance from the registry
            Naming.unbind(RegistrySDService.REGISTRY_SERVICE_NAME);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            //Shutdown rmi
            UnicastRemoteObject.unexportObject(registrySDService, false);
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        ServerInformation serverInformation;

        switch (request.getString(REQUEST)) {
            case SERVER:
                putServersInResponse(response);

                serverInformation = getServerFullInformationFromRequest(request);
                serversInformation.add(serverInformation);

                System.out.print("Server Request, details " + serverInformation + " -> ");
                break;
            case CLIENT:
                if (serversInformation.isEmpty()) break;

                serverInformation = findBestServerForClient();

                response.put(IP, serverInformation.getIp());
                response.put(TCP_PORT, serverInformation.getTcpPort());

                putResponseInDatagramPacket(response, datagramPacket);

                registrySDService.sendNotificationToListeners("Client connected to server " + serverInformation);

                System.out.print("Client Request, connect to Server " + serverInformation + " -> ");
                break;
            case CLIENT_DISCONNECTED:
                serverInformation = getServerTCPInformationFromRequest(request);
                clientDisconnected(serverInformation);

                registrySDService.sendNotificationToListeners("Client disconnected from server " + serverInformation);

                System.out.print("Client Disconnected Request, affected Server " + serverInformation + " -> ");
                break;
            default:
                break;
        }
    }

    private void handleNotifications(JSONObject request) {
        switch (request.getString(REQUEST)) {
            case SERVER:
                serverConnected(getServerTCPInformationFromRequest(request));
                break;
            case SERVER_DISCONNECTED:
                serverDisconnected(getServerTCPInformationFromRequest(request));
                break;
            default:
                break;
        }
    }

    private void putServersInResponse(JSONObject response) {
        JSONArray servers = new JSONArray();
        for (ServerInformation server : serversInformation) {
            servers.put(getServerInformationJSON(server));
        }
        response.put(SERVERS, servers);

        putResponseInDatagramPacket(response, datagramPacket);
    }

    private JSONObject getServerInformationJSON(ServerInformation serverInformation) {
        JSONObject server = new JSONObject();
        server.put(IP, serverInformation.getIp());
        server.put(TCP_PORT, serverInformation.getTcpPort());
        return server;
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

    private void putResponseInDatagramPacket(JSONObject response, DatagramPacket datagramPacket) {
        byte[] bArray = response.toString().getBytes();
        datagramPacket.setData(bArray, 0, bArray.length);
    }

    private void sendNotificationAndWaitForResponse(JSONObject notification, ServerInformation serverInformation) throws CountExceededException {
        CountExceededException countExceededException = new CountExceededException(NUMBER_OF_CONNECTION_ATTEMPTS);

        DatagramPacket datagramPacket;
        try {
            datagramPacket = new DatagramPacket(new byte[datagramPacketSize], datagramPacketSize,
                    serverInformation.getAddress(), serverInformation.getUdpPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                putResponseInDatagramPacket(notification, datagramPacket);

                notificationsDatagramSocket.send(datagramPacket);

                notificationsDatagramSocket.receive(datagramPacket);

                JSONObject response = getRequestFromPacket(datagramPacket);

                ServerInformation server = getServerTCPInformationFromRequest(response);

                if (!server.equals(serverInformation)) {
                    System.out.println("Response from wrong server!");
                    continue;
                }

                return;
            } catch (IOException e) {
                System.out.println("Could not send " + notification.getString(REQUEST) +
                        " notification to Server " + serverInformation + " Counter: "
                        + countExceededException.getCounter() +
                        " Limit: " + countExceededException.getLimit()
                );
                countExceededException.incrementCounter();
            } catch (JSONException je) {
                System.out.println("Wrong response format, needs IP and PORT!");
            }
        }
    }

    private void sendNotificationToServers(JSONObject notification, ServerInformation sender, boolean sendToSender) {
        notification.put(IP, sender.getIp());
        notification.put(TCP_PORT, sender.getTcpPort());

        for (ServerInformation server : serversInformation) {
            //Check if it needs to send the notification to the sender
            if (!sendToSender) {
                if (server.equals(sender)) {
                    continue;
                }
            }

            try {
                System.out.println("Sending notification " + notification.getString(REQUEST)
                        + " to Server -> " + server);
                sendNotificationAndWaitForResponse(notification, server);
                System.out.println("Notification " + notification.getString(REQUEST) + " sent successfully!\n");
            } catch (CountExceededException e) {
                System.out.println("Could not send " + notification.getString(REQUEST) +
                        " notification to Server " + sender + "\n");
                e.printStackTrace();
            }

        }
    }

    private JSONObject getRequestFromPacket(DatagramPacket datagramPacket) {
        String jsonRequest = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

        return new JSONObject(jsonRequest);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                byte[] bArray = new byte[datagramPacketSize];
                datagramPacket = new DatagramPacket(bArray, bArray.length);

                requestsDatagramSocket.receive(datagramPacket);

                JSONObject request = getRequestFromPacket(datagramPacket);

                handleRequest(request);

                requestsDatagramSocket.send(datagramPacket);

                System.out.println("Packet received from " + datagramPacket.getAddress().getHostAddress()
                        + ":" + datagramPacket.getPort() + "\n"
                );

                handleNotifications(request);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void periodicPing(ServerInformation serverInformation) {
        for (ServerInformation server : serversInformation) {
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
    public void serverConnected(ServerInformation serverInformation) {
        JSONObject notification = new JSONObject();
        notification.put(REQUEST, SERVER_CONNECTED);

        sendNotificationToServers(notification, serverInformation, false);
        registrySDService.sendNotificationToListeners("Server " + serverInformation + " connected!");
    }

    @Override
    public void serverDisconnected(ServerInformation serverInformation) {
        if (removeServerFromQueue(serverInformation)) {
            JSONObject notification = new JSONObject();
            notification.put(REQUEST, SERVER_DISCONNECTED);
            System.out.println("Server " + serverInformation + " is no longer active!\n");
            sendNotificationToServers(notification, serverInformation, true);
            registrySDService.sendNotificationToListeners("Server " + serverInformation + " disconnected!");
            //Check if primary server disconnected
            if (serverInformation.equals(serverPingHandler.getPrimaryServer())) {
                System.out.println("Primary Server " + serverInformation + " Disconnected!\n");
                serverPingHandler.setPrimaryServer(null);
            }
        }
    }

    @Override
    public void primaryServer(ServerInformation serverInformation) {
        JSONObject notification = new JSONObject();
        notification.put(REQUEST, PRIMARY_SERVER);
        sendNotificationToServers(notification, serverInformation, true);
    }

    @Override
    public void shutdownServer(ServerInformation serverInformation) {
        JSONObject notification = new JSONObject();
        notification.put(REQUEST, SHUTDOWN_SERVER);
        notification.put(IP, serverInformation.getIp());
        notification.put(TCP_PORT, serverInformation.getTcpPort());

        try {
            sendNotificationAndWaitForResponse(notification, serverInformation);
            //Warn other servers that server disconnected
            serverDisconnected(serverInformation);
        } catch (CountExceededException e) {
            System.out.println("Invalid server!");
        }
    }

    public synchronized boolean shutdownServer(String ip, int port) {
        ServerInformation serverInformation = new ServerInformation(ip, port);

        for (ServerInformation server : getServersInformation()) {
            if (server.equals(serverInformation)) {
                shutdownServer(server);
                return true;
            }
        }

        return false;
    }
}
