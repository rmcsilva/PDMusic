package sample.communication;

import org.json.JSONObject;
import sample.ServersDirectoryInformation;
import sample.exceptions.CountExceededException;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static sample.JSONConstants.REQUEST;

public class ServersDirectoryCommunication implements ServersDirectoryInformation {

    private String serversDirectoryIP;

    private ServerInformation serverInformation;

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    public ServersDirectoryCommunication(String serversDirectoryIP, ServerInformation serverInformation) throws CountExceededException {
        this.serversDirectoryIP = serversDirectoryIP;
        this.serverInformation = serverInformation;
        connectToServersDirectory();
    }

    public void connectToServersDirectory() throws CountExceededException {
        CountExceededException countExceededException = new CountExceededException(3);

        while (true) {
            try {
                connectToServersDirectory(serversDirectoryIP);
                receiveServerInformation();
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

    @Override
    public void connectToServersDirectory(String sdIP) throws IOException {
        InetAddress serversDirectoryAddress = InetAddress.getByName(sdIP);
        System.out.println("Connecting to Servers Directory at " + serversDirectoryAddress.getHostAddress());

        JSONObject request = new JSONObject();
        request.put(REQUEST, SERVER);
        request.put(IP, serverInformation.getIp());
        request.put(PORT, serverInformation.getPort());

        byte[] bArray = request.toString().getBytes();

        datagramPacket = new DatagramPacket(bArray, bArray.length, serversDirectoryAddress, serversDirectoryPort);
        datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(socketsTimeout);
        datagramSocket.send(datagramPacket);
    }

    private void receiveServerInformation() throws IOException {
        System.out.println("Waiting for ServersDirectory Response...");

        byte[] bArray = new byte[datagramPacketSize];
        datagramPacket = new DatagramPacket(bArray, bArray.length);
        datagramSocket.receive(datagramPacket);

        datagramSocket.close();
    }

}

