package sampleRMI;

import sampleRMI.communication.MusicServicecontroller;

import java.rmi.RemoteException;
import java.util.Scanner;

public final class CommandController extends Thread {
    private boolean isAlive = true;
    private MusicServicecontroller musicServicecontroller;

    public CommandController(MusicServicecontroller musicServicecontroller) {
        this.musicServicecontroller = musicServicecontroller;
    }

    @Override
    public void run() {
        Scanner myObj = new Scanner(System.in);
        while (isAlive) {
            System.out.print("Enter command: ");
            String command = myObj.nextLine();

            if (command.equalsIgnoreCase("shutdown")) {
                isAlive = false;
                try {
                    musicServicecontroller.exit();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}