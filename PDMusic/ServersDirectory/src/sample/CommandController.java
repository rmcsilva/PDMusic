package sample;

import java.io.IOException;
import java.util.Scanner;

public final class CommandController extends Thread {

    private Scanner in;

    private boolean isAlive = true;

    CommunicationHandler communicationHandler;

    public CommandController(CommunicationHandler communicationHandler){
        this.communicationHandler = communicationHandler;
    }

    @Override
    public void run() {
        in = new Scanner(System.in);
        while(isAlive){
            System.out.println("Enter command: ");
            String command = in.nextLine().trim();

            if(command.equalsIgnoreCase("shutdown")){
                shutdown();
            }
        }
    }

    public void shutdown() {
        isAlive = false;
        communicationHandler.shutdown();
        try {
            System.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.flush();
    }
}
