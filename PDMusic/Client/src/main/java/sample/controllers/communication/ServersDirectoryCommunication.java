package sample.controllers.communication;

import org.json.JSONObject;
import sample.ServersDirectoryInformation;
import sample.controllers.communication.Exceptions.NoServerAvailable;
import sample.exceptions.NoServersDirectory;
import sample.exceptions.CountExceededException;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static sample.JSONConstants.REQUEST;

public class ServersDirectoryCommunication implements ServersDirectoryInformation {

    private String serversDirectoryIP;

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    public ServersDirectoryCommunication(String serversDirectoryIP) {
        this.serversDirectoryIP = serversDirectoryIP;
    }

    public ServerInformation connectToServersDirectory() throws NoServersDirectory, NoServerAvailable {
        CountExceededException countExceededException = new CountExceededException(3);

        while (true) {
            try {
                connectToServersDirectory(serversDirectoryIP);
                return receiveServerInformation();
            } catch (IOException e) {
                try {
                    System.out.println("Could not connect to serversDirectory! Counter: "
                            + countExceededException.getCounter() +
                            " Limit: " + countExceededException.getLimit()
                    );
                    countExceededException.incrementCounter();
                } catch (CountExceededException cee) {
                    throw new NoServersDirectory();
                }
            }
        }
    }

    @Override
    public void connectToServersDirectory(String sdIP) throws IOException {
        InetAddress serversDirectoryAddress = InetAddress.getByName(sdIP);
        System.out.println("Connecting to Servers Directory at " + serversDirectoryAddress.getHostAddress());

        JSONObject request = new JSONObject();
        request.put(REQUEST, CLIENT);

        byte[] bArray = request.toString().getBytes();

        datagramPacket = new DatagramPacket(bArray, bArray.length, serversDirectoryAddress, serversDirectoryPort);
        datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(socketsTimeout);
        datagramSocket.send(datagramPacket);
    }

    private ServerInformation receiveServerInformation() throws IOException, NoServerAvailable {
        System.out.println("Waiting for Server Information...");

        byte[] bArray = new byte[datagramPacketSize];
        datagramPacket = new DatagramPacket(bArray, bArray.length);
        datagramSocket.receive(datagramPacket);

        String response = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        JSONObject json = new JSONObject(response);

        //If there is not IP on the response then that are no servers
        if (!json.has(IP)) {
            throw new NoServerAvailable();
        }

        String ip = json.getString(IP);
        int port = json.getInt(PORT);

        datagramSocket.close();

        return new ServerInformation(ip, port);
    }

}
