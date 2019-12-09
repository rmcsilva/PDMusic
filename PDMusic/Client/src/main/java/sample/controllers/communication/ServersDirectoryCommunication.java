package sample.controllers.communication;

import org.json.JSONObject;
import sample.ServersDirectoryInformation;
import sample.controllers.communication.Exceptions.NoServerAvailable;
import sample.exceptions.NoServersDirectory;
import sample.exceptions.CountExceededException;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.*;

import static sample.JSONConstants.REQUEST;

public class ServersDirectoryCommunication implements ServersDirectoryInformation {

    private InetAddress serversDirectoryAddress;

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    private JSONObject serversDirectoryResponse;

    public ServersDirectoryCommunication(String serversDirectoryIP) throws UnknownHostException {
        serversDirectoryAddress = InetAddress.getByName(serversDirectoryIP);
    }

    public ServerInformation connectToServersDirectory() throws NoServersDirectory, NoServerAvailable, SocketException {
        datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(socketsTimeout);

        connectToServersDirectory(serversDirectoryAddress);
        return receiveServerInformation();
    }

    @Override
    public void connectToServersDirectory(InetAddress serversDirectoryAddress) throws NoServersDirectory {
        System.out.println("Connecting to Servers Directory at " + serversDirectoryAddress.getHostAddress());

        JSONObject request = new JSONObject();
        request.put(REQUEST, CLIENT);

        try {
            sendPacketAndWaitForResponse(serversDirectoryAddress, request);
        } catch (CountExceededException e) {
            throw new NoServersDirectory();
        }
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

        String response = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        serversDirectoryResponse = new JSONObject(response);
    }

    private ServerInformation receiveServerInformation() throws NoServerAvailable {
        System.out.println("Getting Server Information...");

        //If there is not IP on the response then that are no servers
        if (!serversDirectoryResponse.has(IP)) {
            throw new NoServerAvailable();
        }

        String ip = serversDirectoryResponse.getString(IP);
        int port = serversDirectoryResponse.getInt(TCP_PORT);

        datagramSocket.close();

        return new ServerInformation(ip, port);
    }

}
