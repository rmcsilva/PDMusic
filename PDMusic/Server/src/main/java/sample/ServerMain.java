package sample;

import sample.exceptions.NoServersDirectory;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws NoServersDirectory, IOException {
        System.out.println("Server main!");

        if (args.length != 5) {
            System.out.println("Sintaxe: java Server <sdIP> <dbIP> <dbUsername> <dbPassword> <NIC Multicast>");
            return;
        }

        ServerController serverController = new ServerController(args[0], args[4]);
        serverController.start();

        CommandController commandManager = new CommandController(serverController);
        commandManager.start();

    }
}
