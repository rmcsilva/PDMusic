package sample;

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

        try {
            ServerController serverController = new ServerController(args[0]);
            serverController.start();
        } catch (CountExceededException e) {
            System.out.println("Servers Directory is not running!");
            e.printStackTrace();
            return;
        }
    }
}
