package sampleRMI.communication;

import sampleRMI.CommandController;
import sampleRMI.communication.interfaces.MusicInterface;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class MusicServicecontroller extends UnicastRemoteObject implements MusicInterface {

    protected List<Remote> workers;
    private String mServerName;

    protected MusicServicecontroller(List<Remote> workers, String mServerName) throws RemoteException {
        this.workers = workers;
        this.mServerName = mServerName;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Sintax: java MusicServiceController <sIP> <serverName>");
            return;
        }

        System.setProperty("java.rmi.hostname", args[0]);

        List<Remote> workers = new ArrayList<>();
        String serverName = args[1];

        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            System.out.println("Registry started at " + Registry.REGISTRY_PORT + " port!");
        } catch (RemoteException e) {
            System.out.println("Remote Error: " + e);
            System.exit(1);
        }

        try {
            MusicServicecontroller musicService = new MusicServicecontroller(workers, serverName);
            System.out.println("Music Service started!");

            //Regista o servico para que os clientes possam encontrá-lo, ou seja encontrar a sua referência remota
            Naming.rebind(serverName, musicService);
            System.out.println("Service bound to " + serverName + "!");

            CommandController commandManager = new CommandController(musicService);
            commandManager.start();
            System.out.println("Command system online!");

        } catch (RemoteException e) {
            System.out.println("Remote Error: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            System.exit(1);
        }

    }

    public void exit() throws RemoteException {
        try {
            //Remove o registo do servidor no registry
            Naming.unbind(mServerName);

            //Retira o nosso serviço do runtime RMI
            UnicastRemoteObject.unexportObject(this, true);

            System.out.println(mServerName + " server going offline.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
