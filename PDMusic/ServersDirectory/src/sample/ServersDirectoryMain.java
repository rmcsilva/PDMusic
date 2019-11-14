package sample;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static sample.JSONConstants.REQUEST;
import static sample.ServersDirectoryInformation.*;

public class ServersDirectoryMain {

    public static void main(String[] args) throws IOException {
        System.out.println("Servers Directory Main!");
        System.out.println("Receiving datagram packets!");

        CommunicationHandler communicationHandler = new CommunicationHandler();
        //TODO: In the future will need to be daemon
        //communicationHandler.setDaemon(true);
        communicationHandler.start();

        //TODO: Text interface to get info from communication handler

    }
}
