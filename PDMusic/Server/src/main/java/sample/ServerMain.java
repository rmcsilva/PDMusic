package sample;

//import sample.MySQLAcess;
import org.json.JSONObject;
import sample.communication.ClientCommunication;
import sample.communication.ServersDirectoryCommunication;
import sample.exceptions.CountExceededException;
import sample.models.ServerInformation;

import java.io.IOException;
import java.net.*;

import static sample.JSONConstants.REQUEST;
import static sample.ServersDirectoryInformation.*;

public class ServerMain {

    public static void main(String[] args) throws CountExceededException, IOException {
        System.out.println("Server main!");

        if(args.length != 4){
            System.out.println("Sintaxe: java Server <sdIP> <dbIP> <dbUsername> <dbPassword>");
            return;
        }

        ServerSocket serverSocket = new ServerSocket(0);

        String serverAddress = InetAddress.getLocalHost().getHostAddress();
        int serverPort = serverSocket.getLocalPort();

        System.out.println("Server running at " + InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort());

        ServerInformation serverInformation = new ServerInformation(serverAddress, serverPort);

        ServersDirectoryCommunication serversDirectoryCommunication = new ServersDirectoryCommunication(args[0], serverInformation);

        while (true) {
            System.out.println("Connecting to client");
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(new ClientCommunication(socket));
            thread.start();
        }
    }
}
