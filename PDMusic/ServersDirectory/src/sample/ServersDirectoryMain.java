package sample;

import java.io.IOException;

public class ServersDirectoryMain {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Sintaxe: java ServersDirectory <rmiIP>");
            return;
        }

        System.out.println("Servers Directory Main!");
        System.out.println("Receiving datagram packets!");

        CommunicationHandler communicationHandler = new CommunicationHandler(args[0]);
        communicationHandler.start();

        CommandController commandController = new CommandController(communicationHandler);
        commandController.start();
    }
}
