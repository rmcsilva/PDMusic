package sample;

import java.util.Scanner;

public final class CommandController extends Thread {
    private boolean isAlive = true;
    private ServerController serverController;

    public CommandController(ServerController serverController){
        this.serverController = serverController;
    }

    @Override
    public void run() {
        Scanner myObj = new Scanner(System.in);
        while(isAlive){
            System.out.println("Enter command: ");
            String command = myObj.nextLine();

            if(command.equalsIgnoreCase("shutdown")){
                isAlive = false;
                serverController.shutdown();
            }
        }
    }
}