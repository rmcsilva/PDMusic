package sample;

//import sample.MySQLAcess;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        int PORT = 8080;
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Waiting for a connection on " + PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                if (serverSocket != null) {
                    socket = serverSocket.accept();
                }
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            if (socket != null) {
                new echoThread(socket).start();
            }
        }
    }


}

