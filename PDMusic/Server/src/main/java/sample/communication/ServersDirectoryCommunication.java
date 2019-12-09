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

public class ServersDirectoryCommunication implements ServersDirectoryInformation, sample.ServersDirectoryCommunication {

    private InetAddress serversDirectoryAddress;

    private ServerInformation serverInformation;

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    public ServersDirectoryCommunication(String serversDirectoryIP, ServerInformation serverInformation) throws NoServersDirectory, IOException {
        this.serverInformation = serverInformation;
        serversDirectoryAddress = InetAddress.getByName(serversDirectoryIP);

        datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(socketsTimeout);

        connectToServersDirectory(serversDirectoryAddress);
    }

    @Override
    public void connectToServersDirectory(InetAddress serversDirectoryAddress) throws NoServersDirectory {
        System.out.println("Connecting to Servers Directory at " + serversDirectoryAddress.getHostAddress());

        JSONObject request = new JSONObject();
        request.put(REQUEST, SERVER);
        addServerInformationToRequest(serverInformation, request);

        try {
            sendPacketAndWaitForResponse(serversDirectoryAddress, request);
        } catch (CountExceededException e) {
            throw new NoServersDirectory();
        }
    }

    private void addServerInformationToRequest(ServerInformation serverInformation, JSONObject request) {
        request.put(IP, serverInformation.getIp());
        request.put(PORT, serverInformation.getPort());
    }

    private void sendPacketAndWaitForResponse(InetAddress serversDirectoryAddress, JSONObject request) throws CountExceededException {
        CountExceededException countExceededException = new CountExceededException(NUMBER_OF_CONNECTION_ATTEMPTS);

        while (true) {
            try {
                sendRequestToServersDirectory(serversDirectoryAddress, request);
                receiveServersDirectoryResponse();
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

    private void sendRequestToServersDirectory(InetAddress serversDirectoryAddress, JSONObject request) throws IOException {
        byte[] bArray = request.toString().getBytes();
        datagramPacket = new DatagramPacket(bArray, bArray.length, serversDirectoryAddress, serversDirectoryPort);
        datagramSocket.send(datagramPacket);
    }

    private void receiveServersDirectoryResponse() throws IOException {
        System.out.println("Waiting for ServersDirectory Response...");

        byte[] bArray = new byte[datagramPacketSize];
        datagramPacket = new DatagramPacket(bArray, bArray.length);
        datagramSocket.receive(datagramPacket);
    }

    @Override
    public void clientDisconnected(ServerInformation serverInformation) {
        JSONObject request = new JSONObject();
        request.put(REQUEST, CLIENT_DISCONNECTED);
        addServerInformationToRequest(serverInformation, request);

        try {
            System.out.println("Sending Client Disconnected Request to Servers Directory!");
            sendPacketAndWaitForResponse(serversDirectoryAddress, request);
            System.out.println("Servers Directory Confirmed Client Disconnected Request!");
        } catch (CountExceededException e) {
            System.out.println("Could not send Client Disconnected Request to ServersDirectory!");
        }
    }
    }

}

