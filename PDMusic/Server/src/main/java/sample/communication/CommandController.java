package sample.communication;

import sample.ServerController;

import java.util.Scanner;

public class CommandController implements Runnable{
    private boolean isAlive;
    ServerController controller;

    public CommandController(Boolean isAlive, ServerController controller){
        this.isAlive = isAlive;
        this.controller = controller;
    }

    @Override
    public void run() {
        Scanner myObj = new Scanner(System.in);
        while(isAlive){
            System.out.println("Enter command: ");
            String command = myObj.nextLine();

            if(command.equals("shutdown")){
                isAlive = false;
                controller.shutdown();
            }
        }
    }
}