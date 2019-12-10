package sample;

import sample.communication.CommandController;
import sample.exceptions.NoServersDirectory;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain {

    public static void main(String[] args) throws NoServersDirectory, IOException{
        System.out.println("Server main!");

        if (args.length != 4) {
            System.out.println("Sintaxe: java Server <sdIP> <dbIP> <dbUsername> <dbPassword>");
            return;
        }

        ServerController serverController = new ServerController(args[0]);
        serverController.start();

        Thread commandManager = new Thread(new CommandController(true, serverController));
        commandManager.start();

    }
}
