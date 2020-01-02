package sample;

import java.io.IOException;
import java.util.Scanner;

public final class CommandController extends Thread {

    private Scanner in;

    private boolean isAlive = true;

    private ServerController serverController;

    public CommandController(ServerController serverController){
        this.serverController = serverController;
    }

    @Override
    public void run() {
        in = new Scanner(System.in);
        while(isAlive){
            System.out.println("Enter command: ");
            String command = in.nextLine().trim();

            if(command.equalsIgnoreCase("shutdown")){
                serverController.shutdown();
            }
        }
    }

    public void shutdown() {
        isAlive = false;
        try {
            System.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.flush();
    }
}